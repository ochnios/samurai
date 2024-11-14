package pl.ochnios.samurai.model.entities.document.chunk;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
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
import pl.ochnios.samurai.model.dtos.document.chunk.ChunkDto;
import pl.ochnios.samurai.model.entities.PatchableEntity;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.generator.CustomUuidGenerator;
import pl.ochnios.samurai.model.mappers.ChunkMapper;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "chunks")
public class Chunk implements PatchableEntity {

    public static final int MAX_CHUNK_LENGTH = 8192;

    @Id
    @CustomUuidGenerator
    private UUID id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private DocumentEntity document;

    private int position;

    @Nationalized
    @Column(length = MAX_CHUNK_LENGTH)
    private String content;

    @Setter(AccessLevel.NONE)
    private int length;

    @Builder.Default
    @UpdateTimestamp
    private Instant updatedAt = Instant.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chunk that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public PatchDto getPatchDto() {
        var chunkMapper = Mappers.getMapper(ChunkMapper.class);
        return chunkMapper.map(this);
    }

    @Override
    public void apply(PatchDto patchDto) {
        var chunkPatchDto = (ChunkDto) patchDto;
        position = chunkPatchDto.getPosition();
        content = chunkPatchDto.getContent();
    }

    public void setContent(String content) {
        this.content = content;
        this.length = content != null ? content.length() : 0;
    }

    public static class ChunkBuilder {

        private int length = 0;

        public ChunkBuilder content(String content) {
            this.content = content;
            this.length = content != null ? content.length() : 0;
            return this;
        }
    }
}
