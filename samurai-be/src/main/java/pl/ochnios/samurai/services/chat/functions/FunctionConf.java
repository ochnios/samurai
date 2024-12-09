package pl.ochnios.samurai.services.chat.functions;

import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import pl.ochnios.samurai.services.DocumentService;
import pl.ochnios.samurai.services.chat.ChatContext;
import pl.ochnios.samurai.services.chat.SearchService;

@Configuration
public class FunctionConf {

    @Bean("search")
    @Description(
            """
Searches documents and returns relevant text fragments based on conversation context, query and keywords.
@param conversationSummary - brief context of the ongoing conversation
@param question - specific query or topic for which the search is being conducted
@param keywords - related keywords to broaden search scope
@return array of document fragments (may include partially relevant matches)""")
    public Function<SearchFunction.Request, SearchFunction.Response> search(
            ChatContext chatContext, SearchService searchService) {
        return new SearchFunction(chatContext, searchService);
    }

    @Bean("getDocuments")
    @Description(
            """
Fetches documents that are available for searching.
Should be used only when explicitly asked about available documents. In other cases use search tool.
@return an array of document summaries (title and description)""")
    public Function<GetDocumentsFunction.Request, GetDocumentsFunction.Response> getDocuments(
            ChatContext chatContext, DocumentService documentService) {
        return new GetDocumentsFunction(chatContext, documentService);
    }

    @Bean("getDocument")
    @Description(
            """
Fetches specific document content by title.
@param title - exact title of the document to fetch
@return requested document content""")
    public Function<GetDocumentFunction.Request, GetDocumentFunction.Response> getDocument(
            ChatContext chatContext, DocumentService documentService) {
        return new GetDocumentFunction(chatContext, documentService);
    }
}
