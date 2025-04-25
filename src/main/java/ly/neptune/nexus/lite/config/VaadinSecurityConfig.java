package ly.neptune.nexus.lite.config;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class VaadinSecurityConfig implements WebMvcConfigurer {

    /**
     * Configure security headers at the server level to ensure they are applied.
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerCustomizer() {
        return factory -> factory.addInitializers(servletContext -> {
            // Add Content-Security-Policy header for Vaadin
            servletContext.setInitParameter("org.atmosphere.cpr.cometSupport.maxInactiveActivity", "30000");
            
            // Set CSP header directly on the servlet context
            servletContext.setInitParameter(
                "org.springframework.web.servlet.DispatcherServlet.initParameters.contentSecurityPolicy",
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data:; " +
                "font-src 'self' data:; " +
                "connect-src 'self' ws: wss:; " +
                "frame-src 'self';"
            );
        });
    }
}
