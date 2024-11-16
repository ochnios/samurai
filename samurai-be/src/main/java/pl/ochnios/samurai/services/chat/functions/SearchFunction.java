package pl.ochnios.samurai.services.chat.functions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.ochnios.samurai.services.chat.ChatContext;
import pl.ochnios.samurai.services.chat.SearchService;
import pl.ochnios.samurai.services.chat.dto.DocumentPart;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
        chatContext.setSources(chunks);
        var fragments = chunks.stream()
                .map(ch -> new DocumentPart(ch.getDocumentTitle(), ch.getContent()))
                .toList();
        return new Response(fragments);
    }

    private List<String> getPhrases(Request request) {
        List<String> phrases = new ArrayList<>();
        if(request.conversationSummary != null) {
            phrases.add(request.conversationSummary);
        }
        if(request.question != null) {
            phrases.add(request.question);
        }
        if(request.tags != null) {
            phrases.add(request.tags);
        }
        return phrases;
    }

    public record Request(String conversationSummary, String question, String tags) {}

    public record Response(List<DocumentPart> fragments) {}
}
