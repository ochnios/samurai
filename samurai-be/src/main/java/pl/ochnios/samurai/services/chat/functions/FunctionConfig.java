package pl.ochnios.samurai.services.chat.functions;

import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import pl.ochnios.samurai.services.chat.ChatContext;
import pl.ochnios.samurai.services.chat.SearchService;

@Configuration
public class FunctionConfig {

    @Bean("search")
    @Description(
            """
Searches documents and returns relevant text fragments based on conversation context, query and tags.
@param conversationSummary - brief context of the ongoing conversation
@param question - specific query or topic for which the search is being conducted
@param tags - related keywords to broaden search scope
@returns array of document fragments (may include partially relevant matches)""")
    public Function<SearchFunction.Request, SearchFunction.Response> search(
            ChatContext chatContext, SearchService searchService) {
        return new SearchFunction(chatContext, searchService);
    }
}
