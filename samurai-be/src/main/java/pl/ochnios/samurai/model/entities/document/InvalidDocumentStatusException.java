package pl.ochnios.samurai.model.entities.document;

import pl.ochnios.samurai.commons.exceptions.ApplicationException;

public class InvalidDocumentStatusException extends ApplicationException {

    public InvalidDocumentStatusException(String message) {
        super(message);
    }
}
