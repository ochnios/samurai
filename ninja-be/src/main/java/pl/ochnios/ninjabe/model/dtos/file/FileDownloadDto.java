package pl.ochnios.ninjabe.model.dtos.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadDto extends FileDto {

    @ToString.Exclude
    private byte[] content;
}
