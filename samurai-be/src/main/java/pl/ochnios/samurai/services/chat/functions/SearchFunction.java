package pl.ochnios.samurai.services.chat.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk;
import pl.ochnios.samurai.services.chat.ChatContext;
import pl.ochnios.samurai.services.chat.SearchService;
import pl.ochnios.samurai.services.chat.dto.DocumentPart;

@Slf4j
@RequiredArgsConstructor
public class SearchFunction implements Function<SearchFunction.Request, SearchFunction.Response> {

    private final ChatContext chatContext;
    private final SearchService searchService;

    @Override
    public Response apply(Request request) {
        var phrases = getPhrases(request);
        log.info("Search phrases: {}", phrases);
        var chunks = searchService.search(phrases);

        var parts = enrichContext(chunks);
        log.info("Found {} results", parts.size());

        return new Response(parts);
    }

    private List<String> getPhrases(Request request) {
        List<String> phrases = new ArrayList<>();
        if (request.conversationSummary != null) {
            phrases.add(request.conversationSummary);
        }
        if (request.question != null) {
            phrases.add(request.question);
        }
        if (request.keywords != null) {
            phrases.add(request.keywords);
        }
        return phrases;
    }

    private List<DocumentPart> enrichContext(List<EmbeddedChunk> chunks) {
        List<DocumentPart> parts = new ArrayList<>();
        for (var chunk : chunks) {
            var part = DocumentPart.fromEmbeddedChunk(chunk);
            if (chatContext.addDocument(UUID.fromString(chunk.getDocumentId()), part.toMarkdown())) {
                parts.add(part);
            } else {
                log.warn("Context limit exceeded, ignoring chunk {}", chunk.getId());
            }
        }

        return parts;
    }

    public record Request(String conversationSummary, String question, String keywords) {}

    public record Response(List<DocumentPart> fragments) {}
}
