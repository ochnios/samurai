package pl.ochnios.samurai.config;

import static pl.ochnios.samurai.commons.AppConstants.HTTP_400;
import static pl.ochnios.samurai.commons.AppConstants.HTTP_401;
import static pl.ochnios.samurai.commons.AppConstants.HTTP_404;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.ochnios.samurai.commons.swagger.AppErrorCustomizer;
import pl.ochnios.samurai.commons.swagger.GenericNamesConverter;
import pl.ochnios.samurai.commons.swagger.JsonPatchCustomizer;

// spotless:off
@OpenAPIDefinition(info = @Info(title = "${docs.info.title}", description = "${docs.info.description}", version = "${docs.info.version}", contact = @Contact(name = "${docs.info.contact.name}", url = "${docs.info.contact.url}", email = "${docs.info.contact.email}")))
// spotless:on
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@Configuration
public class SwaggerConf {

    @Bean
    public GenericNamesConverter genericNamesConverter(ObjectMapper objectMapper) {
        return new GenericNamesConverter(objectMapper);
    }

    @Bean
    public JsonPatchCustomizer patchRequestCustomizer() {
        return new JsonPatchCustomizer();
    }

    @Bean
    public AppErrorCustomizer appErrorCustomizer() {
        Map<String, String> codesWithDescriptions = new HashMap<>();
        codesWithDescriptions.put(HTTP_400, "Bad request");
        codesWithDescriptions.put(HTTP_401, "Unauthorized");
        codesWithDescriptions.put(HTTP_404, "Resource not found");
        return new AppErrorCustomizer(codesWithDescriptions);
    }
}
