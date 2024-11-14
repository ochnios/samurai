package pl.ochnios.samurai.model.entities.document;

import static pl.ochnios.samurai.model.entities.document.DocumentStatus.ACTIVE;
import static pl.ochnios.samurai.model.entities.document.DocumentStatus.ARCHIVED;
import static pl.ochnios.samurai.model.entities.document.DocumentStatus.UPLOADED;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import pl.ochnios.samurai.model.dtos.PatchDto;
import pl.ochnios.samurai.model.dtos.document.DocumentDto;
import pl.ochnios.samurai.model.entities.PatchableEntity;
import pl.ochnios.samurai.model.entities.document.chunk.Chunk;
import pl.ochnios.samurai.model.entities.file.FileEntity;
import pl.ochnios.samurai.model.entities.generator.CustomUuidGenerator;
import pl.ochnios.samurai.model.entities.user.User;
import pl.ochnios.samurai.model.mappers.DocumentMapper;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Entity
@Table(name = "documents")
public class DocumentEntity extends FileEntity implements PatchableEntity {

    @Id
    @CustomUuidGenerator
    private UUID id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @ToString.Exclude
    @OrderBy("position asc")
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Chunk> chunks = new ArrayList<>();

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
    @Column(nullable = false)
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
        var documentMapper = Mappers.getMapper(DocumentMapper.class);
        return documentMapper.map(this);
    }

    @Override
    public void apply(PatchDto patchDto) {
        var documentPatchDto = (DocumentDto) patchDto;
        title = documentPatchDto.getTitle();
        description = documentPatchDto.getDescription();
        validateStatusChange(status, documentPatchDto.getStatus());
        status = documentPatchDto.getStatus();
    }

    private void validateStatusChange(DocumentStatus current, DocumentStatus requested) {
        if (current.equals(requested)) {
            return;
        }

        if (requested == null) {
            throw new InvalidDocumentStatusException("Document status must not be null");
        }

        if (!requested.equals(ACTIVE) && !requested.equals(ARCHIVED) && !requested.equals(UPLOADED)) {
            throw new InvalidDocumentStatusException("Status '" + requested.name() + "' cannot be assigned manually");
        }

        if (!current.equals(ACTIVE) && !current.equals(ARCHIVED)) {
            throw new InvalidDocumentStatusException("Status '" + current.name() + "' cannot be changed manually");
        }
    }

    public Resource asResource() {
        return new ByteArrayResource(getContent()) {
            @Override
            public String getFilename() {
                return getName();
            }
        };
    }
}
