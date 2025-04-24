   /*
    * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
    */
package ly.neptune.nexus.lite.service.cbs.flexcube.v14_3.util;

import jakarta.xml.soap.*;
import ly.neptune.nexus.lite.service.cbs.flexcube.v14_3.config.FCUBSConfigProperties;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class SOAPHelper {

    /*
     * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
     */

    private final FCUBSConfigProperties config;

    public SOAPHelper(FCUBSConfigProperties config) {
        this.config = config;
    }


    // Dynamic Branch code is when I don't want to use the default branch code


    /**
     * Adds the common header to the SOAP envelope.
     * (dynamicBranchCode is when you don't want to use the default branch code)
     * @param envelope
     * @param operation
     * @param service
     * @param requestType
     * @param dynamicBranchCode
     * @return
     * @throws SOAPException
     */
    public SOAPElement addCommonHeader(SOAPEnvelope envelope, String operation,String service, String requestType, String dynamicBranchCode) throws SOAPException {

        String branch = config.getHeader().getBranch();

        if(dynamicBranchCode != null && !dynamicBranchCode.isEmpty()){
            branch = dynamicBranchCode;
        }

        SOAPElement queryRequest = envelope.getBody().addChildElement(requestType, "fcub");

        // Build the FCUBS_HEADER block with shared properties
        SOAPElement fcubsHeader = queryRequest.addChildElement("FCUBS_HEADER", "fcub");
        fcubsHeader.addChildElement("SOURCE", "fcub").addTextNode(config.getHeader().getSource());
        fcubsHeader.addChildElement("UBSCOMP", "fcub").addTextNode(config.getHeader().getUbscomp());
        fcubsHeader.addChildElement("USERID", "fcub").addTextNode(config.getHeader().getUserId());
        fcubsHeader.addChildElement("BRANCH", "fcub").addTextNode(branch);
        fcubsHeader.addChildElement("SERVICE", "fcub").addTextNode(service);
        fcubsHeader.addChildElement("OPERATION", "fcub").addTextNode(operation);
        return queryRequest;
    }


    public  String convertSOAPMessageToString(SOAPMessage message) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            return outputStream.toString("UTF-8");
        } catch (Exception e) {
            return "Error converting SOAPMessage to String: " + e.getMessage();
        }
    }

    /**
     * Fetches the value of a given tag from within an XML node.
     *
     * @param node    The XML node to search in.
     * @param tagName The tag name to fetch the value for.
     * @return The value of the tag, or null if not found.
     */
    public String getElementValue(org.w3c.dom.Node node, String tagName) {
        var element = ((org.w3c.dom.Element) node).getElementsByTagName(tagName).item(0);
        return element != null ? element.getTextContent() : null;
    }

    /*
     * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
     */
}