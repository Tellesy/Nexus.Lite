   /*
    * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
    */
package ly.neptune.nexus.lite.service.cbs.flexcube.v14_3;

import jakarta.xml.soap.*;
import lombok.extern.slf4j.Slf4j;
import ly.neptune.nexus.lite.service.cbs.dto.CbsFundsTransferRequest;
import ly.neptune.nexus.lite.service.cbs.dto.CbsFundsTransferResponse;
import ly.neptune.nexus.lite.service.cbs.flexcube.v14_3.config.FCUBSConfigProperties;
import ly.neptune.nexus.lite.service.cbs.flexcube.v14_3.util.SOAPHelper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TellerTransactionOperationsHandler {
    /*
     * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
     */
    private final FCUBSConfigProperties config;
    private final SOAPHelper soapHelper;

    public TellerTransactionOperationsHandler(FCUBSConfigProperties config, SOAPHelper soapHelper) {
        this.config = config;
        this.soapHelper = soapHelper;
    }


    public CbsFundsTransferResponse reverseTransaction(CbsFundsTransferRequest requestDto) {
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            try (SOAPConnection soapConnection = soapConnectionFactory.createConnection()) {
                SOAPMessage soapRequest = createSOAPRequest(requestDto, RTOperation.REVERSE_TRANSACTION);
                String wsdlUrl = config.getRTServiceWsdlUrl();
                SOAPMessage soapResponse = soapConnection.call(soapRequest, wsdlUrl);
                return processSOAPResponse(soapResponse,RTOperation.REVERSE_TRANSACTION);
            }
        } catch (Exception e) {
            log.error("Error executing transaction: {}", e.getMessage());
            System.err.println("Error executing transaction: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Creates the SOAP request message.
     * It builds the envelope and uses the SOAPHelper to add the common header,
     * then appends the Teller Transaction-specific body.
     *
     * @param requestDto the DTO with transaction data
     * @param operation  the operation ("CreateTransaction")
     * @return the constructed SOAPMessage
     * @throws Exception if an error occurs during message creation
     */
    private SOAPMessage createSOAPRequest(CbsFundsTransferRequest requestDto, RTOperation operation) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // Build SOAP envelope and declare namespaces
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("fcub", "http://fcubs.ofss.com/service/FCUBSRTService");

        // Use the common helper to add the shared header.
        // It adds the FCUBS_HEADER block and returns the root request element.

        String branch = requestDto.getDebtorBranch() != null ? requestDto.getDebtorBranch() : config.getHeader().getBranch();
        SOAPElement requestElement = soapHelper.addCommonHeader(
                envelope,
                operation.getOperationValue(),
                config.getRTService(),
                getRequestType(operation),
                requestDto.getDebtorBranch()
        );

        // Append the Teller Transaction-specific body
        addTellerTransactionBody(requestElement, requestDto,operation);


        soapMessage.saveChanges();

        return soapMessage;
    }

    /**
     * Appends the Teller Transaction body elements (non-optional required fields) to the request.
     *
     * @param requestElement the SOAP element returned by addCommonHeader (the root request element)
     * @param requestDto     the transaction request DTO
     * @throws Exception if an error occurs during body element creation
     */
    private void addTellerTransactionBody(SOAPElement requestElement, CbsFundsTransferRequest requestDto, RTOperation operation) throws Exception {
          SOAPElement bodyElement = requestElement.addChildElement("FCUBS_BODY", "fcub");
          // Add the Transaction-Details block


          switch (operation) {
              case REVERSE_TRANSACTION -> addTellerTransactionBodyForReverseTransaction( bodyElement, requestDto);
              default -> throw new RuntimeException("Unsupported operation: " + operation);
          }


    }



    private void addTellerTransactionBodyForReverseTransaction(SOAPElement bodyElement, CbsFundsTransferRequest requestDto) throws Exception {

        SOAPElement txnDetails = bodyElement.addChildElement("Transaction-Details", "fcub");


        txnDetails.addChildElement("FCCREF", "fcub").addTextNode(requestDto.getFccRef());

    }

    /**
     * Returns the request type for the given operation.
     *
     * @param operation the operation to perform
     * @return the request type for the operation
     */
    private String getRequestType(RTOperation operation) {
        return switch (operation) {
            case REVERSE_TRANSACTION-> "REVERSETRANSACTION_FSFS_REQ";
            default -> throw new RuntimeException("Unsupported operation: " + operation);
        };
    }

    /**
     * Processes the SOAP response message and maps required response elements into a CbsFundsTransferResponse.
     *
     * @param soapResponse the SOAPMessage response from the service
     * @return the mapped CbsFundsTransferResponse
     * @throws Exception if processing fails
     */
    private CbsFundsTransferResponse processSOAPResponse(SOAPMessage soapResponse,RTOperation operation) throws Exception {
        CbsFundsTransferResponse responseDto = new CbsFundsTransferResponse();

        try {
            SOAPBody body = soapResponse.getSOAPBody();
            log.info("Response: {}", soapHelper.convertSOAPMessageToString(soapResponse));


            SOAPElement resElement = null;
            switch (operation) {
                case REVERSE_TRANSACTION -> resElement = (SOAPElement) body.getElementsByTagName("REVERSETRANSACTION_FSFS_RES").item(0);
                default -> throw new RuntimeException("Unsupported operation: " + operation.getOperationValue() + " in processSOAPResponse");
            }

            // resElement = (SOAPElement) body.getElementsByTagName("CREATETRANSACTION_FSFS_RES").item(0);
            if (resElement == null) {
                log.error("No response element found");
                responseDto.setSuccess(false);
                responseDto.setErrorCode("ERR");
                responseDto.setErrorMsg("No response element found");

                return responseDto;
                // throw new RuntimeException("No response element found");
            }
            SOAPElement fcubsBody = (SOAPElement) resElement.getElementsByTagName("FCUBS_BODY").item(0);
            if (fcubsBody == null) {
                log.error("No FCUBS_BODY found in response");

                responseDto.setSuccess(false);
                responseDto.setErrorCode("ERR");
                responseDto.setErrorMsg("No FCUBS_BODY found in response");
                return responseDto;
            }

            // Check if there is an error response
            SOAPElement errorElement = (SOAPElement) fcubsBody.getElementsByTagName("FCUBS_ERROR_RESP").item(0);
            if (errorElement != null) {
                SOAPElement errorDetail = (SOAPElement) errorElement.getElementsByTagName("ERROR").item(0);
                if (errorDetail != null) {
                    String errorCode = soapHelper.getElementValue(errorDetail, "ECODE");
                    String errorDesc = soapHelper.getElementValue(errorDetail, "EDESC");
                    String errorMessage = String.format("%s", errorDesc);
                    log.error(errorMessage);

                    responseDto.setSuccess(false);
                    responseDto.setErrorMsg(errorMessage);
                    responseDto.setErrorCode(errorCode);
                    return responseDto;
                }
            }



            SOAPElement warningElement = (SOAPElement) fcubsBody.getElementsByTagName("WARNING").item(0);
            if (warningElement != null) {
                String warningCode = soapHelper.getElementValue(warningElement, "WCODE");
                String warningDesc = soapHelper.getElementValue(warningElement, "WDESC");
                String warningMessage = String.format("%s", warningDesc);
                log.warn(warningMessage);
                responseDto.setSuccess(true);
                responseDto.setErrorMsg(warningMessage);
                responseDto.setErrorCode(warningCode);
                return responseDto;
            }else
            {
                responseDto.setSuccess(false);
                responseDto.setErrorCode("ERR");
                responseDto.setErrorMsg("NO RESPONSE ELEMENT FOUND IN RESPONSE");
                return responseDto;
            }



        }
        catch (Exception e) {
            responseDto.setSuccess(false);
            responseDto.setErrorCode("ERR");
            responseDto.setErrorMsg("Error processing SOAP response: " + e.getMessage());
            log.error("Error processing SOAP response: {}", e.getMessage());
            return responseDto;
        }
    }



    private enum RTOperation {
        REVERSE_TRANSACTION("ReverseTransaction"); // Assign "ReverseTransaction"

        private final String operationValue;

        RTOperation(String operationValue) {
            this.operationValue = operationValue;
        }

        public String getOperationValue() {
            return operationValue;
        }
    }


    /*
     * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
     */
}
