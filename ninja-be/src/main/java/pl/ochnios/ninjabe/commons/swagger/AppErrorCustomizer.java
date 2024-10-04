package pl.ochnios.ninjabe.commons.swagger;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomizer;
import pl.ochnios.ninjabe.model.dtos.AppError;

public class AppErrorCustomizer implements OpenApiCustomizer {

    private final Map<String, String> codesWithDescriptions;

    public AppErrorCustomizer(Map<String, String> codesWithDescriptions) {
        this.codesWithDescriptions = codesWithDescriptions;
    }

    @Override
    public void customise(OpenAPI openApi) {
        addAppErrorSchema(openApi);
        addErrorResponses(openApi);
    }

    private void addAppErrorSchema(OpenAPI openApi) {
        final var appErrorSchema = createAppErrorSchema();
        openApi.getComponents().addSchemas("AppError", appErrorSchema);
    }

    private void addErrorResponses(OpenAPI openApi) {
        for (var code : codesWithDescriptions.entrySet()) {
            customizeErrorResponse(openApi, code.getKey(), code.getValue());
        }
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

    private void customizeErrorResponse(OpenAPI openApi, String code, String description) {
        final var mediaType = new MediaType().schema(new Schema<AppError>().$ref("#/components/schemas/AppError"));
        final var content = new Content().addMediaType(APPLICATION_JSON_VALUE, mediaType);
        final var response = new ApiResponse().description(description).content(content);
        openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations()
                .forEach(operation -> {
                    if (operation.getResponses().containsKey(code)) {
                        operation.getResponses().addApiResponse(code, response);
                    }
                }));
    }
}
