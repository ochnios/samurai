package pl.ochnios.ninjabe.model.dtos;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.Data;

@Data
public class AppError {

    private final UUID errorId;
    private final Set<String> errors;

    public static AppError create(UUID errorId, String... errors) {
        return new AppError(errorId, Set.of(errors));
    }

    public static AppError create(UUID errorId, Iterable<String> errors) {
        final var errorSet = StreamSupport.stream(errors.spliterator(), false).collect(Collectors.toSet());
        return new AppError(errorId, errorSet);
    }
}
