package pl.ochnios.samurai.services.chunking.readers;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.DocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import pl.ochnios.samurai.services.chunking.readers.markdown.MarkdownReader;
import pl.ochnios.samurai.services.chunking.readers.markdown.MarkdownReaderConfig;

@Component
public class ReaderFactory {

    @Value("${custom.chunking.maxChunkLength:8000}")
    private Integer maxChunkLength;

    @Value("${custom.chunking.minChunkLength:300}")
    private Integer minChunkLength;

    private MarkdownReaderConfig config;

    @PostConstruct
    public void init() {
        config = MarkdownReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeBlockquote(true)
                .withIncludeCodeBlock(true)
                .withIncludeTable(true)
                .withMinChunkLength(minChunkLength)
                .withMaxChunkLength(maxChunkLength)
                .build();
    }

    public DocumentReader getReader(Resource resource, String contentType) {
        if (contentType.startsWith("text/")) {
            return new MarkdownReader(resource, config);
        } else {
            return new SemanticReader(resource, config);
        }
    }
}
