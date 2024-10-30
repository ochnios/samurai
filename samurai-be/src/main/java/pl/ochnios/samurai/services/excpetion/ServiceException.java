package pl.ochnios.samurai.services.excpetion;

import pl.ochnios.samurai.commons.exceptions.ApplicationException;

public class ServiceException extends ApplicationException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
