package pl.ochnios.samurai.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk;
import pl.ochnios.samurai.services.excpetion.EmbeddingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final VectorStore vectorStore;

    @Value("${custom.search.topK:5}")
    private Integer topK;

    @Value("${custom.search.similarityThreshold:0.5}")
    private Float similarityThreshold;

    public List<EmbeddedChunk> search(String query) {
        final var searchRequest =
                SearchRequest.defaults().withQuery(query).withTopK(topK).withSimilarityThreshold(similarityThreshold);
        try {
            final var results = vectorStore.similaritySearch(searchRequest);
            return results.stream().map(EmbeddedChunk::new).toList();
        } catch (Exception ex) {
            throw new EmbeddingException("Failed to search chunks in vector store", ex);
        }
    }

    public void add(List<EmbeddedChunk> chunks) {
        try {
            final var springDocuments = chunks.stream().map(ch -> (Document) ch).toList();
            vectorStore.add(springDocuments);
            log.info(
                    "Chunks embeddings {} added",
                    chunks.stream().map(EmbeddedChunk::getId).toList());
        } catch (Exception ex) {
            throw new EmbeddingException("Failed to save chunks in vector store", ex);
        }
    }

    public void add(EmbeddedChunk chunk) {
        add(List.of(chunk));
    }

    public void delete(List<EmbeddedChunk> chunks) {
        try {
            final var ids = chunks.stream().map(EmbeddedChunk::getId).toList();
            final var completed = vectorStore.delete(ids);
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
}
