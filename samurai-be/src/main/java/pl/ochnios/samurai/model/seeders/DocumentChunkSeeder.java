package pl.ochnios.samurai.model.seeders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
            final var chunks = createChunks(document, "files/sample-chunks.txt");
            createEmbeddings(chunks, "files/sample-embeddings.txt");
        }
    }

    private List<DocumentChunk> createChunks(DocumentEntity document, String filepath) {
        final var sampleChunksContent = getFileContent(filepath).split("-----");
        List<DocumentChunk> chunks = new ArrayList<>();
        for (int i = 0; i < sampleChunksContent.length; i++) {
            final var chunk = DocumentChunk.builder()
                    .id(UUID.nameUUIDFromBytes((document.getName() + "_chunk_" + i).getBytes()))
                    .document(document)
                    .content(sampleChunksContent[i].trim())
                    .position(i)
                    .build();
            final var savedChunk = chunkRepository.save(chunk);
            chunks.add(savedChunk);
            log.info("Created document chunk: {}", savedChunk);
        }
        return chunks;
    }

    private void createEmbeddings(List<DocumentChunk> chunks, String filepath) {
        final var embeddings = getFileContent(filepath).split("\n");
        if (chunks.size() != embeddings.length) {
            throw new RuntimeException("Document chunks size mismatch, points won't be created");
        }

        for (int i = 0; i < chunks.size(); i++) {
            final var embeddedChunk = chunkMapper.mapToEmbeddedChunk(chunks.get(i));
            final var embedding = getEmbedding(embeddings[i].trim());
            embeddingService.add(embeddedChunk, embedding);
            log.info("Saved chunk in vector store: {}", embeddedChunk.getId());
        }
    }

    private float[] getEmbedding(String line) {
        String arrayStr = line.substring(1, line.length() - 1); // remove [ and ]
        String[] floatValues = arrayStr.split(",\\s*");
        float[] floatArray = new float[floatValues.length];

        for (int i = 0; i < floatValues.length; i++) {
            floatArray[i] = Float.parseFloat(floatValues[i]);
        }

        return floatArray;
    }

    private String getFileContent(String filepath) {
        final var resource = new ClassPathResource(filepath);
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8).trim();
        } catch (IOException ex) {
            throw new ApplicationException("Failed to read file content", ex);
        }
    }
}
