package pl.ochnios.ninjabe.config;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
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
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import pl.ochnios.ninjabe.commons.swagger.GenericNamesConverter;
import pl.ochnios.ninjabe.commons.swagger.JsonPatchCustomizer;
import pl.ochnios.ninjabe.model.dtos.AppError;

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

    @Order(1)
    @Bean
    public OpenApiCustomizer appErrorCustomizer() {
        final var appErrorSchema = createAppErrorSchema();
        return openApi -> openApi.getComponents().addSchemas("AppError", appErrorSchema);
    }

    @Bean
    public GenericNamesConverter genericNamesConverter(ObjectMapper objectMapper) {
        return new GenericNamesConverter(objectMapper);
    }

    @Bean
    public JsonPatchCustomizer patchRequestCustomizer() {
        return new JsonPatchCustomizer();
    }

    @Bean
    public OpenApiCustomizer noContentCustomizer() {
        return emptyResponseCustomizer(HTTP_204, "No content");
    }

    @Bean
    public OpenApiCustomizer badRequestCustomizer() {
        return errorResponseCustomizer(HTTP_400, "Bad request");
    }

    @Bean
    public OpenApiCustomizer unauthorizedCustomizer() {
        return errorResponseCustomizer(HTTP_401, "Unauthorized");
    }

    @Bean
    public OpenApiCustomizer notFoundCustomizer() {
        return errorResponseCustomizer(HTTP_404, "Resource not found");
    }

    private Schema<AppError> createAppErrorSchema() {
        final var errorIdSchema = new UUIDSchema().description("${docs.dto.error.errorId}");
        final var errorsSchema = new ArraySchema()
                .items(new StringSchema())
                .description("${docs.dto.error.errors}")
                .uniqueItems(true);
        final var schema = new Schema<AppError>();
        schema.description("${docs.dto.error}")
                .addProperty("errorId", errorIdSchema)
                .addProperty("errors", errorsSchema);
        return schema;
    }

    private OpenApiCustomizer errorResponseCustomizer(String code, String description) {
        final var mediaType = new MediaType().schema(new Schema<AppError>().$ref("#/components/schemas/AppError"));
        final var content = new Content().addMediaType(APPLICATION_JSON_VALUE, mediaType);
        final var response = new ApiResponse().description(description).content(content);
        return responseCustomizer(code, response);
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
