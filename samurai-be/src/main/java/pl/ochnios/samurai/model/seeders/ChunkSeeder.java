package pl.ochnios.samurai.model.seeders;

import static pl.ochnios.samurai.model.entities.document.DocumentStatus.ACTIVE;

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
import pl.ochnios.samurai.model.entities.document.chunk.Chunk;
import pl.ochnios.samurai.model.mappers.ChunkMapper;
import pl.ochnios.samurai.repositories.ChunkRepository;
import pl.ochnios.samurai.repositories.DocumentRepository;
import pl.ochnios.samurai.services.EmbeddingService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChunkSeeder implements DataSeeder {

    private final EmbeddingService embeddingService;
    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;
    private final ChunkMapper chunkMapper;

    @Override
    @Transactional
    public void seed() {
        var titles = List.of("Sample PDF");
        for (var title : titles) {
            var id = UUID.nameUUIDFromBytes(title.getBytes());
            var document = documentRepository.findById(id);
            if (document.getChunks().isEmpty()) {
                var chunks = createChunks(document, "files/sample-chunks.txt");
                createEmbeddings(chunks, "files/sample-embeddings.txt");
                document.setStatus(ACTIVE);
                documentRepository.save(document);
            } else {
                log.info("Chunks for document {} already exists, cancelling seeding", id);
            }
        }
    }

    private List<Chunk> createChunks(DocumentEntity document, String filepath) {
        var sampleChunksContent = getFileContent(filepath).split("-----");
        List<Chunk> chunks = new ArrayList<>();
        for (int i = 0; i < sampleChunksContent.length; i++) {
            var chunk = Chunk.builder()
                    .id(UUID.nameUUIDFromBytes((document.getName() + "_chunk_" + i).getBytes()))
                    .document(document)
                    .content(sampleChunksContent[i].trim())
                    .position(i)
                    .build();
            var savedChunk = chunkRepository.save(chunk);
            chunks.add(savedChunk);
            log.info("Created document chunk: {}", savedChunk);
        }
        return chunks;
    }

    private void createEmbeddings(List<Chunk> chunks, String filepath) {
        var embeddings = getFileContent(filepath).split("\n");
        if (chunks.size() != embeddings.length) {
            throw new RuntimeException("Document chunks size mismatch, points won't be created");
        }

        for (int i = 0; i < chunks.size(); i++) {
            var embeddedChunk = chunkMapper.mapToEmbeddedChunk(chunks.get(i));
            var embedding = getEmbedding(embeddings[i].trim());
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
        var resource = new ClassPathResource(filepath);
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8).trim();
        } catch (IOException ex) {
            throw new ApplicationException("Failed to read file content", ex);
        }
    }
}
