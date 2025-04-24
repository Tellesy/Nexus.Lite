package ly.neptune.nexus.lite.dto;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionLookupResponse {
    /*
     * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
     */
    @JsonProperty("code")
    private final String code;

    @JsonProperty("Code")
    private final String Code;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("Message")
    private final String Message;

    @JsonProperty("transactionType")
    private final String transactionType;

    @JsonProperty("TransactionType")
    private final String TransactionType;


    private TransactionLookupResponse(String code, String message, String type) {
        this.code = code;
        this.Code = code; // Backwards compatibility with older versions of the API (as per Moamalat Request)
        this.message = message;
        this.Message = message; // Backwards compatibility with older versions of the API (as per Moamalat Request)
        this.transactionType = type;
        this.TransactionType = type;// Backwards compatibility with older versions of the API (as per Moamalat Request)
    }

    public static TransactionLookupResponse error(String code, String msg) {
        return new TransactionLookupResponse(code, msg, null);
    }
    public static TransactionLookupResponse withType(String code, String msg, String type) {
        return new TransactionLookupResponse(code, msg, type);
    }
    /*
     * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
     */
}

