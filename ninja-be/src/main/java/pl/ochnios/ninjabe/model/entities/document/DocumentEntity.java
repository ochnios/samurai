package pl.ochnios.ninjabe.model.entities.document;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;
import pl.ochnios.ninjabe.model.dtos.PatchDto;
import pl.ochnios.ninjabe.model.dtos.document.DocumentDto;
import pl.ochnios.ninjabe.model.entities.PatchableEntity;
import pl.ochnios.ninjabe.model.entities.file.FileEntity;
import pl.ochnios.ninjabe.model.entities.generator.CustomUuidGenerator;
import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.model.mappers.DocumentMapper;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static pl.ochnios.ninjabe.model.entities.document.DocumentStatus.UPLOADED;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Entity
@Table(name = "documents")
public class DocumentEntity extends FileEntity implements PatchableEntity {

    public DocumentEntity(MultipartFile multipartFile) {
        super(multipartFile);
    }

    @Id
    @CustomUuidGenerator
    private UUID id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User uploader;

    @Nationalized
    private String title;

    @Nationalized
    @Column(length = 2048)
    private String description;

    @Builder.Default
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private DocumentStatus status = UPLOADED;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public PatchDto getPatchDto() {
        final var documentMapper = Mappers.getMapper(DocumentMapper.class);
        return documentMapper.map(this);
    }

    @Override
    public void apply(PatchDto patchDto) {
        final var documentPatchDto = (DocumentDto) patchDto;
        title = documentPatchDto.getTitle();
        description = documentPatchDto.getDescription();
    }
}
