package ly.neptune.nexus.lite.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TransactionLookupRequest {
    /*
     * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
     */

    //Sorry the standard look stupid, it is not my fault :( .. YOU KNOW MY WORK I WOULD NEVER DO THIS
    @JsonProperty("HeaderSwitchModel")
    @JsonAlias({ "headerSwitchModel", "HeaderSwitchModel", "headerswitchmodel", "header_switch_model"})
   // @NotNull(message = "HeaderSwitchModel cannot be null")
    @Valid // Enable validation for nested object
    private HeaderSwitchModel headerSwitchModel = new HeaderSwitchModel();

    @JsonProperty("LookUpData")
    @JsonAlias({"lookUpData", "LookupData", "lookupdata", "lookup_data"})
    @NotNull(message = "LookUpData cannot be null")
    @Valid // Enable validation for nested object
    private LookUpData lookUpData;



    @Getter
    @Setter
    //@AllArgsConstructor
    public static class HeaderSwitchModel {
        @JsonProperty("TargetSystemUserID")
        @JsonAlias({"USERID", "UserId", "userId", "targetSystemUserID"}) // Added variations
       // @NotBlank(message = "TargetSystemUserID is required")
        private String targetSystemUserID = "SWITCHUSER";
    }

    @Getter
    @Setter
    public static class LookUpData {
        @JsonProperty("Details")
        @NotNull(message = "Details cannot be null")
        @Valid // Enable validation for nested object
        private Details details;
    }

    @Getter
    @Setter
    public static class Details {
        @JsonProperty("RRN")
        @JsonAlias({"rrn", "Rrn"})
        @NotBlank(message = "RRN is required")
        @Size(min = 12, max = 12, message = "RRN must be exactly 12 digits")
        @Pattern(regexp = "\\d{12}", message = "RRN must contain only digits")
        private String rrn;

        @JsonProperty("STAN")
        @JsonAlias({"stan", "Stan"})
        @NotBlank(message = "STAN is required")
        @Size(min = 6, max = 6, message = "STAN must be exactly 6 digits")
        @Pattern(regexp = "\\d{6}", message = "STAN must contain only digits")
        private String stan;

        @JsonProperty("TXNAMT")
        @JsonAlias({"txn_amt", "TxnAmt", "txnAmt"})
        @NotBlank(message = "TXNAMT is required")
        @Pattern(regexp = "^\\d+$", message = "TXNAMT must contain only digits representing subunits")
        private String txnAmt;

        @JsonProperty("TERMID")
        @JsonAlias({"term_id", "TermId", "termId"})
        @NotBlank(message = "TERMID is required")
        @Size(min = 6, max = 8, message = "TERMID must be between 6 and 8 digits")
        @Pattern(regexp = "\\d{6,8}", message = "TERMID must contain only digits")
        private String termId;

        @JsonProperty("SETLDATE")
        @JsonAlias({"setl_date", "SetlDate", "setlDate"})
        @NotBlank(message = "SETLDATE is required")
        @Pattern(
                regexp = "^(?:0[1-9]|[12][0-9]|3[01])-(?:0[1-9]|1[0-2])-\\d{4}$",
                message = "SETLDATE must follow DD-MM-YYYY"
        )
        private String setlDate;


        @JsonProperty("message_type")
        @JsonAlias({"MESSAGETYPE", "MsgType","messageType","MessageType"})
        @Size(min = 4, max = 4, message = "messageType must be exactly 4 digits if provided")
        @Pattern(regexp = "\\d{4}", message = "messageType must contain only 4 digits if provided")
        private String messageType = "1200";

        @JsonProperty("reverse")
        @JsonAlias({"REVERSE", "Reverse"})
        private Boolean reverse = false;

        /*
         * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
         */
    }
}
