package pl.ochnios.samurai.services.chat.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.ochnios.samurai.model.dtos.document.DocumentDto;
import pl.ochnios.samurai.services.DocumentService;
import pl.ochnios.samurai.services.chat.ChatContext;
import pl.ochnios.samurai.services.chat.dto.DocumentSummary;

@Slf4j
@RequiredArgsConstructor
public class GetDocumentsFunction implements Function<GetDocumentsFunction.Request, GetDocumentsFunction.Response> {

    private final ChatContext chatContext;
    private final DocumentService documentService;

    @Override
    public Response apply(Request request) {
        var documents = documentService.getActiveDocuments();
        var summaries = enrichContext(documents);
        log.info("Fetched {} document summaries", summaries.size());
        return new Response(summaries);
    }

    private List<DocumentSummary> enrichContext(List<DocumentDto> documents) {
        List<DocumentSummary> summaries = new ArrayList<>();
        for (var document : documents) {
            var summary = DocumentSummary.fromDocumentDto(document);
            if (chatContext.addDocument(document.getId(), summary.toMarkdown())) {
                summaries.add(summary);
            } else {
                log.warn("Context limit exceeded, ignoring document {}", document.getId());
            }
        }

        return summaries;
    }

    public record Request(String dummy) {}

    public record Response(List<DocumentSummary> documents) {}
}
