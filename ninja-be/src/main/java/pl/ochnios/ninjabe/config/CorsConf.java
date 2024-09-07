package pl.ochnios.ninjabe.config;

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

import java.util.List;

@Configuration
public class CorsConf {

    @Value("#{'${custom.allowed-origins}'.split(',')}")
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
            final var corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(allowedOrigins);
            corsConfiguration.setAllowedMethods(List.of("GET", "POST"));
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.setAllowCredentials(true);
            final var corsConfigurationSource = new UrlBasedCorsConfigurationSource();
            corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
            corsConfigurer.configurationSource(corsConfigurationSource);
        };
    }
}
