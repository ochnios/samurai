package pl.ochnios.samurai.services.chat.functions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.ochnios.samurai.services.DocumentService;
import pl.ochnios.samurai.services.chat.ChatContext;

import java.util.UUID;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class GetDocumentFunction implements Function<GetDocumentFunction.Request, GetDocumentFunction.Response> {

    private final ChatContext chatContext;
    private final DocumentService documentService;

    @Override
    public Response apply(Request request) {
        var results = documentService.getByTitle(request.title);
        if (results.isEmpty()) {
            return new Response("Can't find document with title '" + request.title + "'");
        }

        var content = enrichContext(results.getFirst().getId());

        return new Response(content);
    }

    private String enrichContext(UUID documentId) {
        var truncated = false;
        var content = documentService.getContentById(documentId);
        var initialLength = content.length();
        while (content.length() > 100 && !chatContext.canAddDocument(content)) {
            content = content.substring(0, content.length() - 100);
            truncated = true;
        }

        if (truncated) {
            content += "...";
            log.warn(
                    "Content of document {} has been truncated from {} to {} characters",
                    documentId,
                    initialLength,
                    content.length());
        }

        if(!chatContext.addDocument(documentId, content)) {
            log.warn("Context limit exceeded, ignoring document {}", documentId);
            return "";
        }

        return content;
    }

    public record Request(String title) {}

    public record Response(String content) {}
}
