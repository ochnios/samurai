package pl.ochnios.samurai.services.chat.dto;

import pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk;

public record DocumentPart(String documentTitle, String documentPart) {

    public static DocumentPart fromEmbeddedChunk(EmbeddedChunk chunk) {
        return new DocumentPart(chunk.getDocumentTitle(), chunk.getContent());
    }

    public String toMarkdown() {
        return documentPart;
    }
}
