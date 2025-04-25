package ly.neptune.nexus.lite.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Configuration for Oracle database.
 * IMPORTANT: The datasource is configured as lazy to avoid connection attempts on startup.
 * It will only be initialized when explicitly requested through the connect button.
 */
@Configuration
public class OracleConfig {

    private final ConfigService configService;
    
    @Autowired
    public OracleConfig(ConfigService configService) {
        this.configService = configService;
    }
    
    /**
     * Creates an Oracle DataSource with lazy initialization.
     * This ensures we only connect when explicitly requested via the Connect button.
     */
    @Bean(name = "oracleDataSource")
    @Lazy
    @SuppressWarnings("unchecked")
    public DataSource oracleDataSource() {
        try {
            // Get connection details from configuration
            Map<String, Object> cfg = configService.loadConfig();
            Map<String, Object> spring = (Map<String, Object>) cfg.get("spring");
            Map<String, Object> ds = (Map<String, Object>) spring.get("datasource");
            Map<String, Object> hikari = (Map<String, Object>) ds.get("hikari");
            
            // Set up HikariDataSource
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl((String) hikari.get("jdbc-url"));
            dataSource.setUsername((String) hikari.get("username"));
            dataSource.setPassword((String) hikari.get("password"));
            dataSource.setDriverClassName((String) hikari.get("driver-class-name"));
            
            System.out.println(">> oracleJdbcTemplate.oracleDataSource = " + dataSource);
            
            return dataSource;
        } catch (Exception e) {
            System.err.println("Error creating Oracle datasource: " + e.getMessage());
            // Return a placeholder datasource that will fail safely if accidentally used
            return new HikariDataSource();
        }
    }
    
    /**
     * Creates a JdbcTemplate for Oracle operations.
     * This is also lazy to avoid startup connections.
     */
    @Bean(name = "oracleJdbcTemplate")
    @Lazy
    public JdbcTemplate oracleJdbcTemplate() {
        return new JdbcTemplate(oracleDataSource());
    }
}
