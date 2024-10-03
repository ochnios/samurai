package pl.ochnios.ninjabe.config;

import static pl.ochnios.ninjabe.commons.AppConstants.HTTP_204;
import static pl.ochnios.ninjabe.commons.AppConstants.HTTP_400;
import static pl.ochnios.ninjabe.commons.AppConstants.HTTP_401;
import static pl.ochnios.ninjabe.commons.AppConstants.HTTP_404;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.ochnios.ninjabe.commons.swagger.AppErrorCustomizer;
import pl.ochnios.ninjabe.commons.swagger.GenericNamesConverter;
import pl.ochnios.ninjabe.commons.swagger.JsonPatchCustomizer;

// spotless:off
@OpenAPIDefinition(info = @Info(
        title = "${docs.info.title}",
        description = "${docs.info.description}",
        version = "${docs.info.version}",
        contact = @Contact(
            name = "${docs.info.contact.name}",
            url = "${docs.info.contact.url}",
            email = "${docs.info.contact.email}")
        )
)
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

    @Bean
    public OpenApiCustomizer noContentCustomizer() {
        return emptyResponseCustomizer(HTTP_204, "No content");
    }

    private OpenApiCustomizer emptyResponseCustomizer(String code, String description) {
        final var response = new ApiResponse().description(description).content(new Content());
        return responseCustomizer(code, response);
    }

    private OpenApiCustomizer responseCustomizer(String code, ApiResponse response) {
        return openApi -> openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations()
                .forEach(operation -> {
                    if (operation.getResponses().containsKey(code)) {
                        operation.getResponses().addApiResponse(code, response);
                    }
                }));
    }
}
