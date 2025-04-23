package ly.neptune.nexus.lite.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ly.neptune.nexus.lite.dto.TransactionLookupRequest;
import ly.neptune.nexus.lite.dto.TransactionLookupResponse;
import ly.neptune.nexus.lite.service.TransactionLookupService;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionLookupController {

    private final TransactionLookupService service;

    @PostMapping("/lookup")
    public ResponseEntity<TransactionLookupResponse> lookup(@RequestBody TransactionLookupRequest req) {
//        var resp = service.lookup(
//                req.getUserId(),
//                req.getRrn(),
//                req.getStan(),
//                req.getTxnAmt(),
//                req.getTermId(),
//                req.getSetlDate(),
//                req.getMessageType(),
//                req.getReverse()
//        );

        // Extract data from the nested structure
        TransactionLookupRequest.HeaderSwitchModel header = req.getHeaderSwitchModel();
        TransactionLookupRequest.Details details = req.getLookUpData().getDetails();

        var resp = service.lookup(
                header.getTargetSystemUserID(),
                details.getRrn(),
                details.getStan(),
                details.getTxnAmt(),
                details.getTermId(),
                details.getSetlDate(),
                details.getMessageType(),
                details.getReverse()
        );

        return ResponseEntity.ok(resp);
    }
}
