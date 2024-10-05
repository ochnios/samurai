package pl.ochnios.ninjabe.commons.exceptions;

import java.util.List;
import lombok.Getter;

@Getter
public class ValidationException extends ApplicationException {

    private final Iterable<String> messages;

    public ValidationException(Iterable<String> messages) {
        super("ValidationException");
        this.messages = messages;
    }

    public ValidationException(String message) {
        super("ValidationException");
        this.messages = List.of(message);
    }
}
