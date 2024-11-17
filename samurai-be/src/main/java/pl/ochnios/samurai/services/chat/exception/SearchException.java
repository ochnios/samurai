package pl.ochnios.samurai.services.chat.exception;

import pl.ochnios.samurai.services.exception.ServiceException;

public class SearchException extends ServiceException {

    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchException(String message) {
        super(message);
    }
}
