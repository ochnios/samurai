package pl.ochnios.samurai.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.samurai.model.dtos.document.chunk.ChunkDto;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.chunk.Chunk;
import pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk;

@Mapper
public interface ChunkMapper {

    ChunkDto map(Chunk chunk);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "position", source = "chunkDto.position")
    @Mapping(target = "content", source = "chunkDto.content")
    @Mapping(target = "document", source = "documentEntity")
    @Mapping(target = "length", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Chunk map(ChunkDto chunkDto, DocumentEntity documentEntity);

    Chunk copy(Chunk chunk);

    default EmbeddedChunk mapToEmbeddedChunk(Chunk chunk) {
        return EmbeddedChunk.builder()
                .id(chunk.getId())
                .content(chunk.getContent())
                .documentId(chunk.getDocument().getId())
                .documentName(chunk.getDocument().getTitle())
                .build();
    }
}
