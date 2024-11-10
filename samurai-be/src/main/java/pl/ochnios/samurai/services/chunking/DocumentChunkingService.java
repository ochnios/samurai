package pl.ochnios.samurai.services.chunking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    protected void processNextDocument() {
        var document = documentRepository.findFirstByStatus(DocumentStatus.UPLOADED);

        if (document == null) {
            return;
        }

        log.info("Processing document {}", document.getId());
        document.setStatus(DocumentStatus.IN_PROGRESS);
        documentRepository.saveAndFlush(document);

        try {
            chunkService.deleteAll(document.getId());
            document.getChunks().clear();

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
