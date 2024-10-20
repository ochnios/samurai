package pl.ochnios.ninjabe.model.mappers;

import org.mapstruct.Mapper;
import pl.ochnios.ninjabe.model.dtos.document.DocumentDto;
import pl.ochnios.ninjabe.model.dtos.document.DocumentUploadDto;
import pl.ochnios.ninjabe.model.entities.document.DocumentEntity;
import pl.ochnios.ninjabe.model.entities.user.User;

@Mapper(uses = {UserMapper.class})
public interface DocumentMapper {

    DocumentDto map(DocumentEntity documentEntity);

    default DocumentEntity map(User uploader, DocumentUploadDto documentUploadDto) {
        return DocumentEntity.builder()
                .multipartFile(documentUploadDto.getFile())
                .uploader(uploader)
                .title(documentUploadDto.getTitle())
                .description(documentUploadDto.getDescription())
                .build();
    }
}
