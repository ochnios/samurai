package pl.ochnios.samurai.services.chat.exception;

import pl.ochnios.samurai.services.exception.ServiceException;

public class ChatException extends ServiceException {

    public ChatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatException(String message) {
        super(message);
    }
}
