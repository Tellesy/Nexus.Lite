package ly.neptune.nexus.lite.service.cbs.dto;

import lombok.Data;

@Data
public class CbsFundsTransferResponse {
    private String xref;
    private String fccRef;
    private String product;       // PRD
    private String branch;        // BRN
    private String txnBranch;     // TXNBRN
    private String txnAccount;    // TXNACC
    private String txnCurrency;   // TXNCCY
    private String txnAmount; // TXNAMT
    private String txnTrn;        // TXNTRN
    private String offsetBranch;  // OFFSETBRN
    private String offsetAccount; // OFFSETACC
    private String offsetCurrency;// OFFSETCCY
//    private BigDecimal offsetAmount; // OFFSETAMT
    private String offsetAmount; // OFFSETAMT
    private String offsetTrn;     // OFFSETTRN
    private String xRate;     // XRATE
    private String lcyAmount; // LCYAMT
    private String txnDate;       // TXNDATE
    private String valDate;       // VALDATE
    private String relCust;       // RELCUST
    private String tcyTotChgAmt; // TCYTOTCHGAMT
    private String narrative;     // NARRATIVE
    private String trackReverse;  // TRACKREVERSE
    private String lcyXRate;  // LCYXRATE
    private String makerId;       // MAKERID
    private String makerStamp;    // MAKERSTAMP
    private String checkerId;     // CHECKERID
    private String checkerStamp;  // CHECKERSTAMP
    private String recStat;       // RECSTAT
    private String authStat;      // AUTHSTAT
    private String drAcc;         // DRACC
    private String txnDrCr;       // TXNDRCR
    private String bookDate;      // BOOKDATE
    private String ft;            // FT
    private String lcyTotChgAmt; // LCYTOTCHGAMT
    private String denmCcy1;      // DENMCCY1
    private String denmAmt1;  // DENMAMT1
    private String denmAmt2;  // DENMAMT2
    private String acctTitle1;    // ACCTITLE1
    private String acctTitle2;    // ACCTITLE2
    private String actAmt;    // ACTAMT
    private String benfName;      // BENFNAME
    private String benfAddr1;     // BENFADDR1
    private String benfAddr2;     // BENFADDR2
    private String benfAddr4;     // BENFADDR4
    private String acctTitle23;   // ACCTITLE23
    private String denomVariance; // DENOM_VARIANCE
    private String advAcc;        // ADV_ACC
    private String chrAcc;        // CHRACC
    private String custName;      // CUSTNAME
    private boolean isSuccess;// Indicates if the transfer was successful
    private String errorMsg;
    private String errorCode;
}
