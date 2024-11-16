package pl.ochnios.samurai.services.chunking;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ochnios.samurai.model.entities.document.DocumentStatus;
import pl.ochnios.samurai.repositories.DocumentRepository;
import pl.ochnios.samurai.services.ChunkService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentChunkingService {

    private final DocumentRepository documentRepository;
    private final DocumentChunker documentChunker;
    private final ChunkService chunkService;

    protected void processNextDocument() {
        var document = documentRepository.findFirstByStatus(DocumentStatus.UPLOADED);

        if (document == null) {
            return;
        }

        log.info("Processing document {}", document.getId());
        document.setStatus(DocumentStatus.IN_PROGRESS);
        documentRepository.save(document);

        try {
            chunkService.deleteAll(document.getId());
            document.setChunks(new ArrayList<>());
            document = documentRepository.save(document);

            var chunks = documentChunker.process(document);
            chunkService.saveAllEmbedded(document.getId(), chunks);
            document.setStatus(DocumentStatus.ACTIVE);

            log.info("Document {} processed successfully", document.getId());
        } catch (Exception ex) {
            document.setStatus(DocumentStatus.FAILED);
            log.error("Failed to process document {}", document.getId(), ex);
        }

        documentRepository.save(document);
    }
}
