package pl.ochnios.samurai.services.exception;

public class EmbeddingException extends ServiceException {

    public EmbeddingException(String message) {
        super(message);
    }

    public EmbeddingException(String message, Throwable cause) {
        super(message, cause);
    }
}
