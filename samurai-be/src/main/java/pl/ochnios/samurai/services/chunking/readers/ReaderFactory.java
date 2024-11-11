package pl.ochnios.samurai.services.chunking.readers;

import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import static pl.ochnios.samurai.model.entities.file.MimeTypes.DOCX;
import static pl.ochnios.samurai.model.entities.file.MimeTypes.PDF;

@Component
public class ReaderFactory {
    public DocumentReader getReader(Resource resource, String contentType) {
        return switch (contentType.toLowerCase()) {
            case PDF, DOCX -> new SemanticReader(resource);
            default -> new TikaDocumentReader(resource);
        };
    }
}
