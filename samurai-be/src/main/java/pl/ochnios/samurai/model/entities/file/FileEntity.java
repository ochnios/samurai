package pl.ochnios.samurai.model.entities.file;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Nationalized;
import org.springframework.web.multipart.MultipartFile;
import pl.ochnios.samurai.commons.exceptions.ApplicationException;

@Getter
@SuperBuilder
@NoArgsConstructor
@ToString
@MappedSuperclass
public abstract class FileEntity {

    @Nationalized
    @Column(nullable = false, updatable = false)
    private String name;

    @Column(updatable = false)
    private String mimeType;

    @Column(updatable = false)
    private long size;

    @ToString.Exclude
    @Column(nullable = false, updatable = false)
    // @Lob TODO this does not work with postgres but is needed for tests with H2 db
    private byte[] content;

    public abstract static class FileEntityBuilder<C extends FileEntity, B extends FileEntityBuilder<C, B>> {
        public B multipartFile(MultipartFile multipartFile) {
            if (multipartFile != null) {
                try {
                    this.name = multipartFile.getOriginalFilename();
                    this.mimeType = multipartFile.getContentType();
                    this.size = multipartFile.getSize();
                    this.content = multipartFile.getBytes();
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
                try {
                    this.name = file.getName();
                    this.size = file.length();
                    this.mimeType =
                            Files.probeContentType(file.getAbsoluteFile().toPath());
                    this.content = Files.readAllBytes(file.toPath());
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
