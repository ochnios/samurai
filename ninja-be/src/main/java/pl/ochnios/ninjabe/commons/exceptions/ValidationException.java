package pl.ochnios.ninjabe.commons.exceptions;

import lombok.Getter;

@Getter
public class ValidationException extends ApplicationException {

    private final Iterable<String> messages;

    public ValidationException(Iterable<String> messages) {
        super("ValidationException");
        this.messages = messages;
    }
}
