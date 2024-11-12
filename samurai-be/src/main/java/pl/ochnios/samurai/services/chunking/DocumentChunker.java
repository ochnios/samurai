package pl.ochnios.samurai.services.chunking;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.stereotype.Service;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk;
import pl.ochnios.samurai.model.mappers.ChunkMapper;
import pl.ochnios.samurai.services.chunking.readers.ReaderFactory;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentChunker {
    private final TextSplitter textSplitter;
    private final ChunkMapper chunkMapper;
    private final ReaderFactory readerFactory;

    List<EmbeddedChunk> process(DocumentEntity documentEntity) {
        var reader = readerFactory.getReader(documentEntity.asResource(), documentEntity.getMimeType());

        var extracted = reader.get();
        var split = textSplitter.split(extracted);

        var chunks = split.stream()
                .map(d -> chunkMapper.mapToEmbeddedChunk(d, documentEntity))
                .toList();

        log.debug("Document {} processed, chunks: {}", documentEntity.getId(), chunks.size());
        return chunks;
    }
}
