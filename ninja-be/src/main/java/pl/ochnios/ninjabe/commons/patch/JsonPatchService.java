package pl.ochnios.ninjabe.commons.patch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ochnios.ninjabe.exceptions.JsonPatchException;
import pl.ochnios.ninjabe.exceptions.ValidationException;
import pl.ochnios.ninjabe.model.dtos.PatchDto;
import pl.ochnios.ninjabe.model.entities.PatchableEntity;

import javax.json.JsonException;
import javax.json.JsonPatch;
import javax.json.JsonStructure;
import java.util.stream.Collectors;

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
        final var patch = apply(toBePatched, jsonPatch);
        validate(patch);
        original.apply(patch);
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

    private void validate(PatchDto patch) {
        final var violations = validator.validate(patch);
        if (!violations.isEmpty()) {
            final var messages = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.toList());
            throw new ValidationException(messages);
        }
    }
}
