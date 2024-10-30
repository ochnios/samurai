package pl.ochnios.samurai.model.seeders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.ochnios.samurai.commons.exceptions.ApplicationException;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunk;
import pl.ochnios.samurai.model.mappers.DocumentChunkMapper;
import pl.ochnios.samurai.repositories.DocumentChunkRepository;
import pl.ochnios.samurai.repositories.DocumentRepository;
import pl.ochnios.samurai.services.EmbeddingService;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentChunkSeeder implements DataSeeder {

    private final EmbeddingService embeddingService;
    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final DocumentChunkMapper chunkMapper;

    @Override
    @Transactional
    public void seed() {
        final var titles = List.of("Sample PDF");
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
            final var savedChunk = chunkRepository.save(chunk);
            embeddingService.add(chunkMapper.mapToEmbeddingChunk(savedChunk));
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
