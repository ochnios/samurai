package pl.ochnios.samurai.services.chunking.readers;

import org.springframework.ai.document.DocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ReaderFactory {
    public DocumentReader getReader(Resource resource, String contentType) {
        return switch (contentType.toLowerCase()) {
            default -> new SemanticReader(resource);
        };
    }
}
