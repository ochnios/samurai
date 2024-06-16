package pl.ochnios.ninjabe.model.dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ApiError {

    private final UUID errorId;
    private final Iterable<String> errors;

    public static ApiError create(UUID errorId, String... errors) {
        return new ApiError(errorId, List.of(errors));
    }
}

