package pl.ochnios.ninjabe.model.entities.document;

import pl.ochnios.ninjabe.commons.exceptions.ApplicationException;

public class InvalidDocumentStatusException extends ApplicationException {

    public InvalidDocumentStatusException(String message) {
        super(message);
    }
}
