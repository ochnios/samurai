package pl.ochnios.samurai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:api-docs.properties")
@Configuration
public class SwaggerPropsConf {}
