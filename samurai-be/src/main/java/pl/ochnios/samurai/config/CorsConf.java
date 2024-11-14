package pl.ochnios.samurai.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConf {

    @Value("#{'${custom.allowed-origins:none}'.split(',')}")
    private List<String> allowedOrigins;

    @Bean
    @Profile("!local")
    public Customizer<CorsConfigurer<HttpSecurity>> csrfCustomizer() {
        return AbstractHttpConfigurer::disable;
    }

    @Bean
    @Profile("local")
    public Customizer<CorsConfigurer<HttpSecurity>> csrfCustomizerLocal() {
        return corsConfigurer -> {
            var corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(allowedOrigins);
            corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE"));
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.setAllowCredentials(true);
            var corsConfigurationSource = new UrlBasedCorsConfigurationSource();
            corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
            corsConfigurer.configurationSource(corsConfigurationSource);
        };
    }
}
