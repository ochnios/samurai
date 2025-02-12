package pl.ochnios.samurai.commons.patch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Validator;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.JsonException;
import javax.json.JsonPatch;
import javax.json.JsonStructure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ochnios.samurai.commons.exceptions.JsonPatchException;
import pl.ochnios.samurai.commons.exceptions.ValidationException;
import pl.ochnios.samurai.model.dtos.PatchDto;
import pl.ochnios.samurai.model.entities.PatchableEntity;

@Service
@RequiredArgsConstructor
public class JsonPatchService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator;

    @PostConstruct
    public void init() {
        objectMapper.registerModule(new JSR353Module());
    }

    public void apply(PatchableEntity original, JsonPatch jsonPatch) {
        var toBePatched = original.getPatchDto();
        validateJsonPatch(toBePatched, jsonPatch);
        var patchDto = apply(toBePatched, jsonPatch);
        validatePatchDto(patchDto);
        original.apply(patchDto);
    }

    private PatchDto apply(PatchDto toBePatched, JsonPatch jsonPatch) {
        try {
            var targetStructure = objectMapper.convertValue(toBePatched, JsonStructure.class);
            var patchedStructure = jsonPatch.apply(targetStructure);
            return objectMapper.convertValue(patchedStructure, toBePatched.getClass());
        } catch (JsonException ex) {
            throw new JsonPatchException(ex.getMessage());
        }
    }

    private void validateJsonPatch(PatchDto toBePatched, JsonPatch jsonPatch) {
        var fields =
                getAllFields(toBePatched.getClass()).stream().collect(Collectors.toMap(Field::getName, field -> field));
        var operations = jsonPatch.toJsonArray();

        for (var operation : operations) {
            var patchPathValue = operation.asJsonObject().get("path");
            if (patchPathValue == null) {
                throw new ValidationException("Field 'path' is required");
            }

            String patchFieldName = patchPathValue.toString().replace("\"", "").replaceFirst("/", "");
            var field = fields.get(patchFieldName);
            if (field == null) {
                throw new ValidationException("Field '" + patchFieldName + "' does not exist in "
                        + toBePatched.getClass().getSimpleName());
            }

            if (field.isAnnotationPresent(NotPatchable.class)) {
                throw new ValidationException("Field '" + field.getName() + "' is not patchable");
            }
        }
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            Collections.addAll(fields, clazz.getDeclaredFields());
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private void validatePatchDto(PatchDto patchDto) {
        var violations = validator.validate(patchDto);
        if (!violations.isEmpty()) {
            var messages = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.toList());
            throw new ValidationException(messages);
        }
    }
}
