package ly.neptune.nexus.lite.service.cbs.flexcube.v14_3.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Component
@ConfigurationProperties(prefix = "fcubs")
public class FCUBSConfigProperties {

    /*
     * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
     */
    private String wsdlUrl;
    @Getter
    private Header header;

    // Getters and Setters
    public String getWsdlUrl() {
        System.out.println("WSDL URL: " + wsdlUrl);
        return wsdlUrl;
    }
    public String getRTService() {return "FCUBSRTService";}

    public String getRTServiceWsdlUrl()
    {
        return wsdlUrl + "/FCUBSRTService/FCUBSRTService?wsdl";
    }

    @Setter
    @Getter
    public static class Header {
        private String userId;
        private String branch;
       // private String service;
        private String source;
        private String ubscomp;

    }

    /*
     * This is the Free and Lite version of nexus, if you want to use the full version, Please contact us.
     */
}