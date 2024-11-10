package pl.ochnios.samurai.model.mappers;

import java.sql.Blob;
import java.sql.SQLException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.ochnios.samurai.commons.exceptions.ApplicationException;
import pl.ochnios.samurai.model.dtos.file.FileDownloadDto;
import pl.ochnios.samurai.model.entities.file.FileEntity;

@Mapper
public interface FileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "content", source = "content", qualifiedByName = "mapFileContent")
    FileDownloadDto mapToDownloadDto(FileEntity fileEntity);

    @Named("mapFileContent")
    default byte[] mapFileContent(Blob blob) {
        try {
            return blob.getBytes(1, (int) blob.length());
        } catch (SQLException ex) {
            throw new ApplicationException("Failed to get file content", ex);
        }
    }
}
