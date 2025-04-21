package ly.neptune.nexus.lite.dto;
import lombok.Getter;

@Getter
public class TransactionLookupResponse {
    private final String code;
    private final String message;
    private final String transactionType;

    private TransactionLookupResponse(String code, String message, String type) {
        this.code = code;
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
