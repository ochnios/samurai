package pl.ochnios.samurai.services.chunking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
public class DocumentChunkingTask {

    private final DocumentChunkingService documentChunkingService;
    private static final Object PROCESSING_LOCK = new Object();

    @Scheduled(
            initialDelayString = "${custom.chunking.initialDelay.seconds}000",
            fixedDelayString = "${custom.chunking.fixedDelay.seconds}000")
    public void processDocuments() {
        synchronized (PROCESSING_LOCK) {
            try {
                documentChunkingService.processNextDocument();
            } catch (Exception ex) {
                log.error("Error during document processing", ex);
            }
        }
    }
}
