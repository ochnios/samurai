package pl.ochnios.samurai.model.seeders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import pl.ochnios.samurai.commons.exceptions.ApplicationException;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunk;
import pl.ochnios.samurai.repositories.DocumentChunkRepository;
import pl.ochnios.samurai.repositories.DocumentRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentChunkSeeder implements DataSeeder {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;

    @Override
    public void seed() {
        final var titles = List.of("Sample PDF", "Sample DOCX");
        for (var title : titles) {
            final var id = UUID.nameUUIDFromBytes(title.getBytes());
            final var document = documentRepository.findById(id);
            createChunks(document, "files/sample-chunks.txt");
        }
    }

    private void createChunks(DocumentEntity document, String filepath) {
        final var sampleChunksContent = getFileContent(filepath).split("-----");
        for (int i = 0; i < sampleChunksContent.length; i++) {
            final var chunk = DocumentChunk.builder()
                    .id(UUID.nameUUIDFromBytes((document.getName() + "_chunk_" + i).getBytes()))
                    .document(document)
                    .content(sampleChunksContent[i].trim())
                    .position(i)
                    .build();
            final var savedChunk = documentChunkRepository.save(chunk);
            log.info("Created document chunk: {}", savedChunk);
        }
    }

    private String getFileContent(String filepath) {
        final var resource = new ClassPathResource(filepath);
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new ApplicationException("Failed to read file content", ex);
        }
    }
}
