package ly.neptune.nexus.lite.dto;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionLookupResponse {

    @JsonProperty("code")
    private final String code;

    @JsonProperty("Code")
    private final String Code;

    @JsonProperty("message")
    private final String message;
    @JsonProperty("transactionType")
    private final String transactionType;


    private TransactionLookupResponse(String code, String message, String type) {
        this.code = code;
        this.Code = code; // Backwards compatibility with older versions of the API (as per Moamalat Request)
        this.message = message;
        this.transactionType = type;
    }

    public static TransactionLookupResponse error(String code, String msg) {
        return new TransactionLookupResponse(code, msg, null);
    }
    public static TransactionLookupResponse withType(String code, String msg, String type) {
        return new TransactionLookupResponse(code, msg, type);
    }
}

