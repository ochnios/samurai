package pl.ochnios.ninjabe.model.mappers;

import org.mapstruct.Mapper;
import pl.ochnios.ninjabe.model.dtos.document.DocumentDto;
import pl.ochnios.ninjabe.model.dtos.document.DocumentUploadDto;
import pl.ochnios.ninjabe.model.entities.document.DocumentEntity;
import pl.ochnios.ninjabe.model.entities.user.User;

@Mapper
public interface DocumentMapper {

    DocumentDto map(DocumentEntity documentEntity);

    default DocumentEntity map(User uploader, DocumentUploadDto documentUploadDto) {
        final var documentEntity = new DocumentEntity(documentUploadDto.getFile());
        return documentEntity.toBuilder()
                .uploader(uploader)
                .title(documentEntity.getTitle())
                .description(documentEntity.getDescription())
                .build();
    }
}
