package pl.ochnios.samurai.model.mappers;

import org.mapstruct.Mapper;
import pl.ochnios.samurai.model.dtos.document.chunk.DocumentChunkDto;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunk;

@Mapper
public interface DocumentChunkMapper {

    DocumentChunkDto map(DocumentChunk documentChunk);
}
