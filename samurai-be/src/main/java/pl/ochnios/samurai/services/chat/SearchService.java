package pl.ochnios.samurai.services.chat;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ochnios.samurai.model.dtos.document.DocumentDto;
import pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk;
import pl.ochnios.samurai.services.DocumentService;
import pl.ochnios.samurai.services.EmbeddingService;
import pl.ochnios.samurai.services.chat.exception.SearchException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final EmbeddingService embeddingService;
    private final DocumentService documentService;

    public List<EmbeddedChunk> search(List<String> queries) {
        if (queries == null || queries.isEmpty()) {
            return List.of();
        }

        var ids = getActiveDocumentIds();
        var futures = createSearchFutures(queries, ids);
        var results = collectResults(futures);
        logResults(queries, results);
        return results;
    }

    private List<UUID> getActiveDocumentIds() {
        return documentService.getActiveDocuments().stream()
                .map(DocumentDto::getId)
                .toList();
    }

    private List<CompletableFuture<List<EmbeddedChunk>>> createSearchFutures(List<String> phrases, List<UUID> ids) {
        return phrases.stream()
                .map(phrase -> embeddingService.searchAsync(phrase, ids))
                .toList();
    }

    private List<EmbeddedChunk> collectResults(List<CompletableFuture<List<EmbeddedChunk>>> futures) {
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return futures.stream()
                .flatMap(future -> {
                    try {
                        return future.get().stream();
                    } catch (InterruptedException | ExecutionException ex) {
                        throw new SearchException("Exception while collecting search results", ex);
                    }
                })
                .collect(Collectors.toMap(
                        EmbeddedChunk::getId,
                        chunk -> chunk,
                        (chunk1, chunk2) -> chunk1.getScore() > chunk2.getScore() ? chunk1 : chunk2))
                .values()
                .stream()
                .sorted(Comparator.comparing(EmbeddedChunk::getScore))
                .toList();
    }

    private void logResults(List<String> queries, List<EmbeddedChunk> results) {
        if (log.isDebugEnabled()) {
            var queriesString = String.join("\n", queries);
            var resultsString =
                    results.stream().map(EmbeddedChunk::formatFoundChunk).collect(Collectors.joining("\n"));
            log.debug("Search for queries:\n{}\nResults ({}):\n{}", queriesString, results.size(), resultsString);
        }
    }
}
