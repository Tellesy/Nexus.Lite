package ly.neptune.nexus.lite.config;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@Profile("prod")
public class OracleDataSourceConfig {



    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource oracleDataSource() {
        return new HikariDataSource();
    }



    @Bean(name = "oracleJdbcTemplate")
    public JdbcTemplate oracleJdbcTemplate(
            @Qualifier("oracleDataSource") DataSource ds) {
        System.out.println(">> oracleJdbcTemplate.oracleDataSource = " + ds);
        JdbcTemplate tpl = new JdbcTemplate(ds);
        return tpl;
    }


}