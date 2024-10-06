package pl.ochnios.ninjabe.commons.patch;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ochnios.ninjabe.commons.exceptions.JsonPatchException;
import pl.ochnios.ninjabe.commons.exceptions.ValidationException;
import pl.ochnios.ninjabe.model.dtos.PatchDto;
import pl.ochnios.ninjabe.model.entities.PatchableEntity;

@Slf4j
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
        final var toBePatched = original.getPatchDto();
        validateJsonPatch(toBePatched, jsonPatch);
        final var patchDto = apply(toBePatched, jsonPatch);
        validatePatchDto(patchDto);
        original.apply(patchDto);
    }

    private PatchDto apply(PatchDto toBePatched, JsonPatch jsonPatch) {
        try {
            final var targetStructure = objectMapper.convertValue(toBePatched, JsonStructure.class);
            final var patchedStructure = jsonPatch.apply(targetStructure);
            return objectMapper.convertValue(patchedStructure, toBePatched.getClass());
        } catch (JsonException ex) {
            throw new JsonPatchException(ex.getMessage());
        }
    }

    private void validateJsonPatch(PatchDto toBePatched, JsonPatch jsonPatch) {
        final var fields =
                getAllFields(toBePatched.getClass()).stream().collect(Collectors.toMap(Field::getName, field -> field));
        final var operations = jsonPatch.toJsonArray();

        for (var operation : operations) {
            final var patchPathValue = operation.asJsonObject().get("path");
            if (patchPathValue == null) {
                throw new ValidationException("Field 'path' is required");
            }

            String patchFieldName = patchPathValue.toString().replace("\"", "").replaceFirst("/", "");
            final var field = fields.get(patchFieldName);
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
        final var violations = validator.validate(patchDto);
        if (!violations.isEmpty()) {
            final var messages = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.toList());
            throw new ValidationException(messages);
        }
    }
}
