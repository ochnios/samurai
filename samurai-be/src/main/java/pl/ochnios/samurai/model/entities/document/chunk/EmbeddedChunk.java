package pl.ochnios.samurai.model.entities.document.chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.ai.document.ContentFormatter;
import org.springframework.ai.document.DefaultContentFormatter;
import org.springframework.ai.document.Document;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmbeddedChunk extends Document {

    public static final String DOCUMENT_ID_KEY = "doc_id";
    public static final String DOCUMENT_TITLE_KEY = "doc_title";
    public static final String DOCUMENT_CONTENT_KEY = "doc_content";

    public EmbeddedChunk(Document document) {
        this(document.getId(), document.getContent(), document.getMetadata());
    }

    public EmbeddedChunk(String id, String content, Map<String, Object> metadata) {
        super(id, content, metadata);
        super.setContentFormatter(defaultFormatter());
    }

    public EmbeddedChunk(String content, Map<String, Object> metadata) {
        super(content, metadata);
        super.setContentFormatter(defaultFormatter());
    }

    public EmbeddedChunk(String content) {
        super(content);
        super.setContentFormatter(defaultFormatter());
    }

    public String getDocumentName() {
        return (String) super.getMetadata().get(DOCUMENT_TITLE_KEY);
    }

    public String getDocumentId() {
        return (String) super.getMetadata().get(DOCUMENT_ID_KEY);
    }

    public static EmbeddedChunkBuilder builder() {
        return new EmbeddedChunkBuilder();
    }

    public static class EmbeddedChunkBuilder extends Document.Builder {
        private String id = null;
        private String content = "";
        private final Map<String, Object> metadata = new HashMap<>();

        public EmbeddedChunkBuilder id(UUID id) {
            this.id = id.toString();
            return this;
        }

        public EmbeddedChunkBuilder content(String content) {
            this.content = content;
            return this;
        }

        public EmbeddedChunkBuilder documentId(UUID documentId) {
            metadata.put(DOCUMENT_ID_KEY, documentId.toString());
            return this;
        }

        public EmbeddedChunkBuilder documentTitle(String documentTitle) {
            metadata.put(DOCUMENT_TITLE_KEY, documentTitle);
            return this;
        }

        public EmbeddedChunk build() {
            return id != null ? new EmbeddedChunk(id, content, metadata) : new EmbeddedChunk(content, metadata);
        }
    }

    public ContentFormatter defaultFormatter() {
        return DefaultContentFormatter.builder()
                .withExcludedEmbedMetadataKeys(DOCUMENT_ID_KEY)
                .build();
    }
}
