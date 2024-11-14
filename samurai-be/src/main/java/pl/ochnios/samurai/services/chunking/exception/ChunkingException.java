package pl.ochnios.samurai.services.chunking.exception;

import pl.ochnios.samurai.services.excpetion.ServiceException;

public class ChunkingException extends ServiceException {

    public ChunkingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChunkingException(String message) {
        super(message);
    }
}
