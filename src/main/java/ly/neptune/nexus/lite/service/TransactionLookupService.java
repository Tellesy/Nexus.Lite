package ly.neptune.nexus.lite.service;


import lombok.extern.slf4j.Slf4j;
import ly.neptune.nexus.lite.dto.TransactionLookupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TransactionLookupService {

    private final JdbcTemplate jdbc;
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

    public TransactionLookupResponse lookup(String userId, String rrn, String stan,
                                            String amtStr, String termId, String setlDate) {
        if (!"SWITCHUSER".equals(userId)) {
            return TransactionLookupResponse.error("E1","Please check user id");
        }
        if (!isDigits(rrn,12)) {
            return TransactionLookupResponse.error("E2","Please check RRN must be 12 digit and not include any characters");
        }
        if (!isDigits(stan,6)) {
            return TransactionLookupResponse.error("E3","Please check STAN must be 6 digit and not include any characters");
        }
        if (!isValidAmount(amtStr)) {
            return TransactionLookupResponse.error("E4","Please check TXNAMT");
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

        BigDecimal amt = new BigDecimal(amtStr);
        String sql =
                "SELECT SUBSTR(MSG_TYPE||'',1,2) AS PC, " +
                        "       DECODE(WORK_PROGRESS,'F','F','S','S','#') AS ST, " +
                        "       TRN_REF_NO " +
                        "  FROM swtbs_txn_log " +
                        " WHERE rrn=? AND stan=? AND txn_amt/1000=? AND term_id=? AND setl_date=? AND msg_type=1200";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, rrn, stan, amt, termId, valueDate);
        if (rows.isEmpty()) {
            rows = jdbc.queryForList(sql.replace("swtbs_txn_log","swtbs_txn_hist"),
                    rrn, stan, amt, termId, valueDate);
        }

        if (rows.size()==1 || rows.size()==2) {
            var row = rows.get(0);
            String status = ((String)row.get("ST")).trim();
            String pc     = ((String)row.get("PC")).trim();
            String refNo  = status.equals("S")?((String)row.get("TRN_REF_NO")).trim():"00-00-00";
            if ("F".equals(status)) {
                return TransactionLookupResponse.withType("R1","Transaction is Failed", pc.equals("21")?"CREDIT":"DEBIT");
            }
            String rtl = jdbc.queryForObject(
                    "SELECT record_stat FROM detbs_rtl_teller WHERE trn_ref_no=?", String.class, refNo
            );
            return switch (rtl) {
                case "V" -> TransactionLookupResponse.withType("R2","Already Reversed", pc.equals("21")?"CREDIT":"DEBIT");
                case "A" -> TransactionLookupResponse.withType("R3","Already Processed", pc.equals("21")?"CREDIT":"DEBIT");
                default  -> TransactionLookupResponse.error("E9","Core bank system error");
            };
        }

        return TransactionLookupResponse.error("R4","Transaction is not Found");
    }

    // helpers
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
        try {
            return StringUtils.hasText(s)
                    && new BigDecimal(s).compareTo(BigDecimal.ZERO)>0
                    && NUM.matcher(s).results().count()<=8
                    && SPEC.matcher(s).results().count()<=1
                    && UPPER.matcher(s).results().count()==0
                    && LOWER.matcher(s).results().count()==0;
        } catch (Exception e) {
            return false;
        }
    }
    private boolean hasValidDate(String s) {
        return StringUtils.hasText(s)
                && DASH.matcher(s).results().count()>=2
                && NUM.matcher(s).results().count()>=8;
    }
}
