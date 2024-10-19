package pl.ochnios.ninjabe.model.entities.document;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import java.io.IOException;
import java.sql.Blob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.web.multipart.MultipartFile;
import pl.ochnios.ninjabe.commons.exceptions.ApplicationException;

@Getter
@NoArgsConstructor
@ToString
@MappedSuperclass
public abstract class FileEntity {

    public FileEntity(MultipartFile multipartFile) {
        if (multipartFile != null) {
            try {
                this.name = multipartFile.getOriginalFilename();
                this.mimeType = multipartFile.getContentType();
                this.size = multipartFile.getSize();
                this.content = BlobProxy.generateProxy(multipartFile.getInputStream(), multipartFile.getSize());
            } catch (IOException ex) {
                throw new ApplicationException("Failed to read multipartFile", ex);
            }
        } else {
            throw new ApplicationException("MultipartFile cannot be null");
        }
    }

    @Nationalized
    @Column(nullable = false, updatable = false)
    private String name;

    @Column(nullable = false, updatable = false)
    private String mimeType;

    @Column(updatable = false)
    private long size;

    @ToString.Exclude
    @Lob
    @Column(nullable = false, updatable = false)
    private Blob content;
}
