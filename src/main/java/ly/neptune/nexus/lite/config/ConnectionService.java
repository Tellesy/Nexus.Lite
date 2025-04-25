package ly.neptune.nexus.lite.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.Map;

@Service
public class ConnectionService {
    private final ConfigService configService;

    public ConnectionService(ConfigService configService) {
        this.configService = configService;
    }

    public boolean testConnection() {
        return testDb() && testWs();
    }

    private boolean testDb() {
        try {
            Map<String, Object> cfg = configService.loadConfig();
            Map<String, Object> spring = (Map<String, Object>) cfg.get("spring");
            if (spring == null) return false;
            Map<String, Object> ds = (Map<String, Object>) spring.get("datasource");
            if (ds == null) return false;
            Map<String, Object> hikari = (Map<String, Object>) ds.get("hikari");
            if (hikari == null) return false;

            HikariConfig hc = new HikariConfig();
            hc.setJdbcUrl((String) hikari.get("jdbc-url"));
            hc.setUsername((String) hikari.get("username"));
            hc.setPassword((String) hikari.get("password"));
            hc.setDriverClassName((String) hikari.get("driver-class-name"));

            Object maxPool = hikari.get("maximum-pool-size");
            if (maxPool instanceof Number) {
                hc.setMaximumPoolSize(((Number) maxPool).intValue());
            }
            Object minIdle = hikari.get("minimum-idle");
            if (minIdle instanceof Number) {
                hc.setMinimumIdle(((Number) minIdle).intValue());
            }
            Object connTimeout = hikari.get("connection-timeout");
            if (connTimeout instanceof Number) {
                hc.setConnectionTimeout(((Number) connTimeout).longValue());
            }
            Object idleTimeout = hikari.get("idle-timeout");
            if (idleTimeout instanceof Number) {
                hc.setIdleTimeout(((Number) idleTimeout).longValue());
            }
            Object maxLifetime = hikari.get("max-lifetime");
            if (maxLifetime instanceof Number) {
                hc.setMaxLifetime(((Number) maxLifetime).longValue());
            }
            Object props = hikari.get("data-source-properties");
            if (props instanceof Map) {
                Map<String, Object> dsp = (Map<String, Object>) props;
                dsp.forEach(hc::addDataSourceProperty);
            }

            try (HikariDataSource ds2 = new HikariDataSource(hc);
                 Connection conn = ds2.getConnection()) {
                return conn.isValid(5);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean testWs() {
        try {
            Map<String, Object> cfg = configService.loadConfig();
            Map<String, Object> fcubs = (Map<String, Object>) cfg.get("fcubs");
            if (fcubs == null) return false;
            String wsdlUrl = (String) fcubs.get("wsdl-url");
            if (wsdlUrl == null) return false;
            URL url = new URL(wsdlUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            return code == 200;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
