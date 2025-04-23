package ly.neptune.nexus.lite.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TransactionLookupRequest {

    @JsonProperty("userId")
    @NotBlank(message = "userId is required")
    private String userId;

    @JsonProperty("rrn")
    @NotBlank(message = "rrn is required")
    @Size(min = 12, max = 12, message = "rrn must be exactly 12 digits")
    @Pattern(regexp = "\\d{12}", message = "rrn must contain only digits")
    private String rrn;

    @JsonProperty("stan")
    @NotBlank(message = "stan is required")
    @Size(min = 6, max = 6, message = "stan must be exactly 6 digits")
    @Pattern(regexp = "\\d{6}", message = "stan must contain only digits")
    private String stan;


    @JsonProperty("txnAmt")
    @NotBlank(message = "txnAmt is required")
    // Updated Pattern: Allow only digits (e.g., "105000")
    @Pattern(regexp = "^\\d+$", message = "txnAmt must contain only digits representing subunits")
    private String txnAmt;


    @JsonProperty("termId")
    @NotBlank(message = "termId is required")
    @Size(min = 6, max = 8, message = "termId must be between 6 and 8 digits")
    @Pattern(regexp = "\\d{6,8}", message = "termId must contain only digits")
    private String termId;

    @JsonProperty("setlDate")
    @NotBlank(message = "setlDate is required")
    @Pattern(
            regexp = "^(?:0[1-9]|[12][0-9]|3[01])-(?:0[1-9]|1[0-2])-\\d{4}$",
            message = "setlDate must follow DD-MM-YYYY"
    )
    private String setlDate;

    @JsonProperty("messageType")
    @Size(min = 4, max = 4, message = "messageType must be exactly 4 digits if provided")
    @Pattern(regexp = "\\d{4}", message = "messageType must contain only 4 digits if provided")
    private String messageType = "1200";

    @JsonProperty("reverse")
    private Boolean reverse = false;


}
