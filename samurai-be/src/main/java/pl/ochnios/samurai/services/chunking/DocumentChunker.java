package pl.ochnios.samurai.services.chunking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk;
import pl.ochnios.samurai.model.mappers.ChunkMapper;
import pl.ochnios.samurai.services.chunking.readers.ReaderFactory;
import pl.ochnios.samurai.services.chunking.splitters.MaxLengthSplitter;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentChunker {

    private final ChunkMapper chunkMapper;
    private final ReaderFactory readerFactory;

    @Value("${custom.chunking.maxChunkLength:8000}")
    private int maxChunkLength;

    List<EmbeddedChunk> process(DocumentEntity documentEntity) {
        var reader = readerFactory.getReader(documentEntity.asResource(), documentEntity.getMimeType());

        var extracted = reader.get();
        var split = splitter().apply(extracted);

        var chunks = split.stream()
                .map(d -> chunkMapper.mapToEmbeddedChunk(d, documentEntity))
                .toList();

        log.debug("Document {} processed, chunks: {}", documentEntity.getId(), chunks.size());
        return chunks;
    }

    private MaxLengthSplitter splitter() {
        return new MaxLengthSplitter(maxChunkLength);
    }
}
