package pl.ochnios.samurai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;

@Configuration
public class HeadersConf {

    @Bean
    @Profile("!local")
    public Customizer<HeadersConfigurer<HttpSecurity>> headersCustomizer() {
        return Customizer.withDefaults();
    }

    @Bean
    @Profile("local")
    public Customizer<HeadersConfigurer<HttpSecurity>> headersCustomizerLocal() {
        return headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
    }
}
