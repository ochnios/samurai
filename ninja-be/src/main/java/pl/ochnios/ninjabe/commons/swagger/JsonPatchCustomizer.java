package pl.ochnios.ninjabe.commons.swagger;

import static pl.ochnios.ninjabe.commons.AppConstants.PATCH_MEDIA_TYPE;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;

public class JsonPatchCustomizer implements OpenApiCustomizer {

    @Override
    public void customise(OpenAPI openApi) {
        addJsonPatchSchema(openApi);
        customiseJsonPatchRequests(openApi);
    }

    private void addJsonPatchSchema(OpenAPI openApi) {
        final var jsonPatchSchema = createJsonPatchSchema();
        openApi.getComponents()
                .addSchemas("JsonPatch", jsonPatchSchema)
                .addSchemas("JsonPatch operation", jsonPatchSchema.getItems());
    }

    private void customiseJsonPatchRequests(OpenAPI openApi) {
        final var mediaType = new MediaType().schema(new Schema<>().$ref("#/components/schemas/JsonPatch"));
        final var content = new Content().addMediaType(PATCH_MEDIA_TYPE, mediaType);
        final var requestBody = new RequestBody().content(content);
        openApi.getPaths().forEach((path, pathItem) -> {
            final var patchOperation = pathItem.getPatch();
            if (patchOperation != null) {
                patchOperation.requestBody(requestBody);
            }
        });
    }

    private ArraySchema createJsonPatchSchema() {
        final var opSchema =
                new StringSchema()._enum(List.of("add", "replace", "remove")).description("${docs.dto.json-patch.op}");
        final var pathSchema = new StringSchema().description("${docs.dto.json-patch.value}");
        final var valueSchema = new ObjectSchema().nullable(true).description("${docs.dto.json-patch.value}");
        final var mapSchema = new MapSchema();
        mapSchema
                .required(List.of("op", "path"))
                .addProperty("op", opSchema)
                .addProperty("path", pathSchema)
                .addProperty("value", valueSchema);
        final var schema = new ArraySchema();
        schema.items(mapSchema).description("${docs.dto.json-patch}");
        return schema;
    }
}
