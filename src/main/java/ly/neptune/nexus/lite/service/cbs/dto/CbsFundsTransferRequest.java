package ly.neptune.nexus.lite.service.cbs.dto;

import lombok.Data;

@Data
public class CbsFundsTransferRequest {
    // A unique transaction reference (e.g., "NEXUSFTNX155579")
    private String xref;

    private String fccRef; //internal reference for the transaction
    // Debitor account ID (mapped to TXNACC)
    private String debtorAccount;
    private String debtorBranch; // Debitor branch ID (mapped to TXNBRN)
    // Creditor account ID (mapped to OFFSETACC)
    private String creditorAccount;
    // Transaction amount (applied to both TXNAMT and OFFSETAMT)
    private String creditorBranch; // Creditor branch ID (mapped to OFFSETBRN)

    private String amount;

    private String currency;

    private String description;

    //Product code (e.g., "NEXUS")
    private String product;
}
