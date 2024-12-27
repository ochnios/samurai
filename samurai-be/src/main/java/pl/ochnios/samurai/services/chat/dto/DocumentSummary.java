package pl.ochnios.samurai.services.chat.dto;

import pl.ochnios.samurai.model.dtos.document.DocumentDto;

public record DocumentSummary(String title, String description) {

    public static DocumentSummary fromDocumentDto(DocumentDto doc) {
        return new DocumentSummary(doc.getTitle(), doc.getDescription());
    }

    public String toMarkdown() {
        return String.format("%s - %s", title, description);
    }
}
