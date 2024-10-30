package pl.ochnios.samurai.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.samurai.model.dtos.document.chunk.DocumentChunkDto;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunk;
import pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk;

@Mapper
public interface DocumentChunkMapper {

    DocumentChunkDto map(DocumentChunk chunk);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "content", source = "chunkDto.id")
    @Mapping(target = "document", source = "documentEntity")
    @Mapping(target = "length", ignore = true)
    DocumentChunk map(DocumentChunkDto chunkDto, DocumentEntity documentEntity);

    default EmbeddedChunk mapToEmbeddingChunk(DocumentChunk chunk) {
        return EmbeddedChunk.builder()
                .id(chunk.getId())
                .content(chunk.getContent())
                .documentId(chunk.getDocument().getId())
                .documentName(chunk.getDocument().getName())
                .build();
    }
}
