package pl.ochnios.samurai.services;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.ValueFactory;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk;
import pl.ochnios.samurai.services.excpetion.EmbeddingException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.VectorsFactory.vectors;
import static pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk.DOCUMENT_CONTENT_KEY;
import static pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk.DOCUMENT_ID_KEY;
import static pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk.DOCUMENT_TITLE_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final QdrantClient qdrantClient; // platform dependant!!!
    private final VectorStore vectorStore;

    @Value("${spring.ai.vectorstore.qdrant.collection-name}")
    private String collectionName;

    @Value("${spring.ai.openai.embedding.options.dimensions}")
    private Integer dimensions;

    @Value("${custom.search.topK:5}")
    private Integer topK;

    @Value("${custom.search.similarityThreshold:0.5}")
    private Float similarityThreshold;

    public List<EmbeddedChunk> search(String query) {
        var searchRequest =
                SearchRequest.defaults().withQuery(query).withTopK(topK).withSimilarityThreshold(similarityThreshold);
        try {
            var results = vectorStore.similaritySearch(searchRequest);
            return results.stream().map(EmbeddedChunk::new).toList();
        } catch (Exception ex) {
            throw new EmbeddingException("Failed to search chunks in vector store", ex);
        }
    }

    public void add(List<EmbeddedChunk> chunks) {
        try {
            var springDocuments = chunks.stream().map(ch -> (Document) ch).toList();
            vectorStore.add(springDocuments);
            var ids = chunks.stream().map(EmbeddedChunk::getId).toList();
            log.info("Chunks embeddings {} added", ids);
        } catch (Exception ex) {
            throw new EmbeddingException("Failed to save chunks in vector store", ex);
        }
    }

    public void add(EmbeddedChunk chunk) {
        add(List.of(chunk));
    }

    public void delete(List<EmbeddedChunk> chunks) {
        try {
            var ids = chunks.stream().map(EmbeddedChunk::getId).toList();
            var completed = vectorStore.delete(ids);
            if (completed.isEmpty() || !completed.get()) {
                log.warn("Vector store delete returned false, chunk ids={}", ids);
            }
            log.info("Chunks embeddings {} deleted", ids);
        } catch (Exception ex) {
            throw new EmbeddingException("Failed to delete chunks from vector store", ex);
        }
    }

    public void delete(EmbeddedChunk chunk) {
        delete(List.of(chunk));
    }

    public void update(List<EmbeddedChunk> chunks) {
        delete(chunks);
        add(chunks);
    }

    public void update(EmbeddedChunk chunk) {
        update(List.of(chunk));
    }

    public void add(EmbeddedChunk chunk, float[] embedding) {
        if (embedding.length != dimensions) {
            throw new EmbeddingException("Chunk dimensions mismatch, point won't be created, id=" + chunk.getId());
        }

        var point = Points.PointStruct.newBuilder()
                .setId(id(UUID.fromString(chunk.getId())))
                .setVectors(vectors(embedding))
                .putAllPayload(createPayload(chunk))
                .build();

        try {
            qdrantClient.upsertAsync(collectionName, List.of(point));
        } catch (Exception ex) {
            throw new EmbeddingException("Failed to save point in vector store", ex);
        }

        log.info("Saved point: {}", chunk.getId());
    }

    private Map<String, JsonWithInt.Value> createPayload(EmbeddedChunk chunk) {
        var docId = ValueFactory.value(chunk.getId());
        var docName = ValueFactory.value(chunk.getDocumentName());
        var docContent = ValueFactory.value(chunk.getContent());
        return Map.of(DOCUMENT_ID_KEY, docId, DOCUMENT_TITLE_KEY, docName, DOCUMENT_CONTENT_KEY, docContent);
    }
}
