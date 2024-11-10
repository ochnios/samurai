package pl.ochnios.samurai.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.samurai.model.dtos.file.FileDownloadDto;
import pl.ochnios.samurai.model.entities.file.FileEntity;

@Mapper
public interface FileMapper {

    @Mapping(target = "id", ignore = true)
    FileDownloadDto mapToDownloadDto(FileEntity fileEntity);
}
