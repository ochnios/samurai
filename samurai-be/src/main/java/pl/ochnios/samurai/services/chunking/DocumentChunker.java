package pl.ochnios.samurai.services.chunking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.stereotype.Service;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk;
import pl.ochnios.samurai.model.mappers.ChunkMapper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentChunker {

    private final TextSplitter textSplitter;
    private final ChunkMapper chunkMapper;

    List<EmbeddedChunk> process(DocumentEntity documentEntity) {
        var tikaReader = new TikaDocumentReader(documentEntity.asResource());
        var extracted = tikaReader.get();
        var split = textSplitter.split(extracted);

        var chunks = split.stream()
                .map(d -> chunkMapper.mapToEmbeddedChunk(d, documentEntity))
                .toList();

        log.debug("Document {} processed, chunks: ", chunks);
        return chunks;
    }
}
