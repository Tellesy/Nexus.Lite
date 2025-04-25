package ly.neptune.nexus.lite.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ly.neptune.nexus.lite.dto.TransactionLookupRequest;
import ly.neptune.nexus.lite.dto.TransactionLookupResponse;
import ly.neptune.nexus.lite.service.TransactionLookupService;

/*
 * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionLookupController {

    private static final Logger log = LoggerFactory.getLogger(TransactionLookupController.class);

    private final TransactionLookupService service;

    @PostMapping("/lookup")
    public ResponseEntity<TransactionLookupResponse> lookup(@RequestBody TransactionLookupRequest req) {
        log.info("Received transaction lookup request: {}", req);
        try {
            // Extract data from the nested structure
            TransactionLookupRequest.HeaderSwitchModel header = req.getHeaderSwitchModel();
            TransactionLookupRequest.Details details = req.getLookUpData().getDetails();
            /*
             * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
             */
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

            log.info("Successfully processed lookup for RRN: {}", details.getRrn());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("Error processing transaction lookup for RRN: {}", req.getLookUpData().getDetails().getRrn(), e);
            // Return a generic 500 error response
            return ResponseEntity.status(500).build();
        }
    }
}
