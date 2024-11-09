package pl.ochnios.samurai.model.mappers;

import org.mapstruct.Mapper;
import pl.ochnios.samurai.model.dtos.document.DocumentDto;
import pl.ochnios.samurai.model.dtos.document.DocumentUploadDto;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.user.User;

@Mapper(uses = {UserMapper.class})
public interface DocumentMapper {

    DocumentDto map(DocumentEntity documentEntity);

    default DocumentEntity map(User user, DocumentUploadDto documentUploadDto) {
        return DocumentEntity.builder()
                .multipartFile(documentUploadDto.getFile())
                .user(user)
                .title(documentUploadDto.getTitle())
                .description(documentUploadDto.getDescription())
                .build();
    }
}
