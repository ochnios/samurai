package pl.ochnios.ninjabe.model.dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AppError {

    private final UUID errorId;
    private final Iterable<String> errors;

    public static AppError create(UUID errorId, String... errors) {
        return new AppError(errorId, List.of(errors));
    }
}
