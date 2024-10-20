package pl.ochnios.ninjabe.model.entities.file;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Nationalized;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.web.multipart.MultipartFile;
import pl.ochnios.ninjabe.commons.exceptions.ApplicationException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.Blob;

@Getter
@SuperBuilder
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

    @Column(updatable = false)
    private String mimeType;

    @Column(updatable = false)
    private long size;

    @ToString.Exclude
    @Lob
    @Column(nullable = false, updatable = false)
    private Blob content;

    public abstract static class FileEntityBuilder<C extends FileEntity, B extends FileEntityBuilder<C, B>> {
        public B multipartFile(MultipartFile multipartFile) {
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
            return self();
        }

        public B file(File file) {
            if (file != null) {
                try (var inputStream = new FileInputStream(file)) {
                    final var bytes = inputStream.readAllBytes();
                    this.name = file.getName();
                    this.size = file.length();
                    this.mimeType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
                    this.content = BlobProxy.generateProxy(new ByteArrayInputStream(bytes), file.length());
                } catch (IOException ex) {
                    throw new ApplicationException("Failed to read file", ex);
                }
            } else {
                throw new ApplicationException("File cannot be null");
            }
            return self();
        }
    }
}
