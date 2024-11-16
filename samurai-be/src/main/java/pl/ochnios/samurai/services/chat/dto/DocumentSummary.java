package pl.ochnios.samurai.services.chat.dto;

import pl.ochnios.samurai.model.dtos.document.DocumentDto;

public record DocumentSummary(String id, String title, String description) {

    public static DocumentSummary fromDocumentDto(DocumentDto doc) {
        return new DocumentSummary(doc.getId().toString(), doc.getTitle(), doc.getDescription());
    }
}
