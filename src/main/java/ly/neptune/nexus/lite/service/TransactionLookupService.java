package ly.neptune.nexus.lite.service;

import lombok.extern.slf4j.Slf4j;
import ly.neptune.nexus.lite.dto.TransactionLookupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; // Import StringUtils

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TransactionLookupService {

    private final JdbcTemplate jdbc;

    // Inject configuration properties
    @Value("${app.datasource.schema-name:#{null}}")
    private String schemaName;

    @Value("${app.datasource.log-table-name:swtbs_txn_log}") // Default value if property is missing
    private String logTableName;

    @Value("${app.datasource.hist-table-name:#{null}}") // Default to null if property is missing or empty
    private String histTableName;

    @Value("${app.datasource.rtl-table-name:detbs_rtl_teller}") // Default value
    private String rtlTableName;


    private static final Pattern NUM   = Pattern.compile("\\d");
    private static final Pattern SPEC  = Pattern.compile("\\.");
    private static final Pattern UPPER = Pattern.compile("[A-Z]");
    private static final Pattern LOWER = Pattern.compile("[a-z]");
    private static final Pattern DASH  = Pattern.compile("-");

    private static final DateTimeFormatter IN_FMT  = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter OUT_FMT = DateTimeFormatter.ofPattern("yyMMdd");

    @Autowired
    public TransactionLookupService(@Qualifier("oracleJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    // Note: Parameters messageType and reverse are present but not used from controller in provided code.
    // Assuming they might be used later or are remnants. Keeping them for now.
    public TransactionLookupResponse lookup(String userId, String rrn, String stan,
                                            String amtStr, String termId, String setlDate, String messageType, boolean reverse) {

        // --- Input Validation (remains the same) ---
        if (!"SWITCHUSER".equals(userId)) {
            return TransactionLookupResponse.error("E1","Please check user id");
        }
        if (!isDigits(rrn,12)) {
            return TransactionLookupResponse.error("E2","Please check RRN must be 12 digit and not include any characters");
        }
        if (!isDigits(stan,6)) {
            return TransactionLookupResponse.error("E3","Please check STAN must be 6 digit and not include any characters");
        }
        // Use the updated isValidAmount validation
        if (!isValidAmount(amtStr)) {
            return TransactionLookupResponse.error("E4","Please check TXNAMT - must be only digits");
        }

        if (!isLength(termId,6,8)) {
            return TransactionLookupResponse.error("E5","Please check TERMID must be 6–8 digits with no special characters");
        }
        if (!hasValidDate(setlDate)) {
            return TransactionLookupResponse.error("E6","Please check txn date");
        }
        LocalDate parsed;
        try {
            parsed = LocalDate.parse(setlDate, IN_FMT);
        } catch (Exception e) {
            return TransactionLookupResponse.error("E7","Date format must be DD-MM-YYYY");
        }
        String valueDate = parsed.format(OUT_FMT);
        // --- End Input Validation ---

        // Use default messageType if empty
        if (!StringUtils.hasText(messageType)) {
            messageType = "1200";
        }



        // Construct fully qualified table names using the helper method
        String qualifiedLogTableName = getQualifiedTableName(logTableName);
        String qualifiedRtlTableName = getQualifiedTableName(rtlTableName);


        // Updated SQL: Compare trimmed txn_amt with the input string amtStr
        String baseSql = "SELECT SUBSTR(MSG_TYPE||'',1,2) AS PC, " +
                "       DECODE(WORK_PROGRESS,'F','F','S','S','#') AS ST, " +
                "       TRN_REF_NO " +
                "  FROM %s " +
                " WHERE rrn=? AND stan=? AND TRIM(LEADING '0' FROM txn_amt)=? AND term_id=? AND setl_date=? AND msg_type=?"; // Use TRIM and pass amtStr

        String logSql = String.format(baseSql, qualifiedLogTableName);

        log.debug("Executing SQL: {} with params: rrn={}, stan={}, amtStr={}, termId={}, valueDate={}, messageType={}",
                logSql, rrn, stan, amtStr, termId, valueDate, messageType); // Log amtStr
        // Pass amtStr directly to the query
        List<Map<String,Object>> rows = jdbc.queryForList(logSql, rrn, stan, amtStr, termId, valueDate, messageType);


        // Check history table ONLY if the first query was empty AND histTableName is configured
        if (rows.isEmpty() && StringUtils.hasText(histTableName)) {
            // Construct qualified name only when needed and if the schema exists
            String qualifiedHistTableName = getQualifiedTableName(histTableName);
            String histSql = String.format(baseSql, qualifiedHistTableName);
            log.debug("Log table query returned no results. Checking history table. SQL: {} with params: rrn={}, stan={}, amt={}, termId={}, valueDate={}, messageType={}",
                    histSql, rrn, stan, amtStr, termId, valueDate, messageType);
            rows = jdbc.queryForList(histSql, rrn, stan, amtStr, termId, valueDate, messageType);
        }


        // Process results if found
        if (!rows.isEmpty()) { // Simplified check: any result (1 or 2) is processed
            var row = rows.get(0); // Process the first row found
            String status = ((String)row.get("ST")).trim();
            String pc     = ((String)row.get("PC")).trim();
            String refNo  = status.equals("S") ? ((String)row.get("TRN_REF_NO")).trim() : "00-00-00"; // Default ref if not 'S'

            if ("F".equals(status)) {
                log.info("Transaction found with status 'F'. RRN: {}, STAN: {}", rrn, stan);
                return TransactionLookupResponse.withType("R1","Transaction is Failed", pc.equals("21")?"CREDIT":"DEBIT");
            }

            // If status is 'S', check the retail teller table
            if ("S".equals(status) && !refNo.equals("00-00-00")) {
                String rtlSql = String.format("SELECT record_stat FROM %s WHERE trn_ref_no=?", qualifiedRtlTableName);
                try {
                    log.debug("Executing RTL SQL: {} with param: refNo={}", rtlSql, refNo);
                    String rtl = jdbc.queryForObject(rtlSql, String.class, refNo);
                    log.info("Transaction found with status 'S', RTL status: {}. RRN: {}, STAN: {}, RefNo: {}", rtl, rrn, stan, refNo);
                    return switch (rtl) {
                        case "V" -> TransactionLookupResponse.withType("R2","Already Reversed", pc.equals("21")?"CREDIT":"DEBIT");
                        case "A" -> TransactionLookupResponse.withType("R3","Already Processed", pc.equals("21")?"CREDIT":"DEBIT");
                        default  -> {
                            log.error("Unexpected RTL status '{}' for RRN: {}, STAN: {}, RefNo: {}", rtl, rrn, stan, refNo);
                            yield TransactionLookupResponse.error("E9","Core bank system error - Unknown RTL status");
                        }
                    };
                } catch (Exception e) {
                    log.error("Error querying RTL table for RRN: {}, STAN: {}, RefNo: {}. Error: {}", rrn, stan, refNo, e.getMessage(), e);
                    return TransactionLookupResponse.error("E8", "Core bank system error - RTL lookup failed");
                }
            } else {
                // Handle cases where status is 'S' but refNo is default, or status is neither 'F' nor 'S'
                log.warn("Transaction found but status is not 'F' and either not 'S' or refNo is default. Status: '{}', RRN: {}, STAN: {}", status, rrn, stan);
                // Depending on requirements, might need a specific error or response here.
                // For now, falling through to "Not Found" might be misleading if a record *was* found but in an unexpected state.
                return TransactionLookupResponse.error("E10", "Transaction found in an inconsistent state");
            }
        }

        // If no rows found after checking log and potentially history table
        log.info("Transaction not found for RRN: {}, STAN: {}", rrn, stan);
        return TransactionLookupResponse.error("R4","Transaction is not Found");
    }

    // --- Helper methods (remain the same) ---
    private boolean isDigits(String s,int len) {
        return StringUtils.hasText(s)
                && s.length()==len
                && NUM.matcher(s).results().count()==len
                && SPEC.matcher(s).results().count()==0
                && UPPER.matcher(s).results().count()==0
                && LOWER.matcher(s).results().count()==0;
    }
    private boolean isLength(String s,int min,int max) {
        return StringUtils.hasText(s)
                && s.length()>=min && s.length()<=max
                && SPEC.matcher(s).results().count()==0;
    }

    private boolean isValidAmount(String s) {
        return StringUtils.hasText(s)
                && NUM.matcher(s).results().count() == s.length() // All characters must be digits
                && !s.isEmpty(); // Ensure it's not empty after potential trim (though @NotBlank helps)
    }

    private boolean hasValidDate(String s) {
        // Basic check - relies on LocalDate.parse for actual validation
        return StringUtils.hasText(s)
                && DASH.matcher(s).results().count()==2; // Check for two dashes
    }

    /**
     * Helper method to construct the fully qualified table name (schema.table)
     * only if a schemaName is configured. Otherwise, returns the base table name.
     * @param tableName The base table name (e.g., "swtbs_txn_log")
     * @return The qualified table name (e.g., "SCHEMA.swtbs_txn_log") or base name.
     */
    private String getQualifiedTableName(String tableName) {
        if (StringUtils.hasText(schemaName)) {
            return schemaName + "." + tableName;
        }
        return tableName;
    }

}
