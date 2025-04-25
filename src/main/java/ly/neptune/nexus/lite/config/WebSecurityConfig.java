package ly.neptune.nexus.lite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

@Configuration
public class WebSecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Let VaadinWebSecurity set base CSP, just add unsafe directives needed
        // String cspValue = "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
        //                   "style-src 'self' 'unsafe-inline';";
                          
        // Let VaadinWebSecurity handle H2 console frames if needed
        // http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permit ALL requests
                .requestMatchers("/**").permitAll()
            );

            // Remove header configurations
            /*
            .headers(headers -> headers
                // We override frameOptions handled by VaadinWebSecurity if H2 console is needed
                .frameOptions(frameOptions -> frameOptions.sameOrigin()) 
                .contentSecurityPolicy(policy -> policy.policyDirectives(cspValue)) // Add our specific CSP directives
            )
            */

            // Remove login/logout configurations
            /*
            .formLogin(form -> form
                .loginPage("/login").permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
            */
    }
}
