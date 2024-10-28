package pl.ochnios.samurai.model.entities.document.chunk;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;
import org.mapstruct.factory.Mappers;
import pl.ochnios.samurai.model.dtos.PatchDto;
import pl.ochnios.samurai.model.dtos.document.chunk.DocumentChunkDto;
import pl.ochnios.samurai.model.entities.PatchableEntity;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.generator.CustomUuidGenerator;
import pl.ochnios.samurai.model.mappers.DocumentChunkMapper;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chunks")
public class DocumentChunk implements PatchableEntity {

    @Id
    @CustomUuidGenerator
    private UUID id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private DocumentEntity document;

    private int position;

    @Nationalized
    @Column(length = 8192)
    private String content;

    @Builder.Default
    @Setter(AccessLevel.NONE)
    private int length = 0;

    @Builder.Default
    @UpdateTimestamp
    private Instant updatedAt = Instant.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentChunk that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public PatchDto getPatchDto() {
        final var documentChunkMapper = Mappers.getMapper(DocumentChunkMapper.class);
        return documentChunkMapper.map(this);
    }

    @Override
    public void apply(PatchDto patchDto) {
        final var chunkPatchDto = (DocumentChunkDto) patchDto;
        content = chunkPatchDto.getContent();
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void updateContentLength() {
        length = content != null ? content.length() : 0;
    }
}
