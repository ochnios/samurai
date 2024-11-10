package pl.ochnios.samurai.services.chunking;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.chunk.EmbeddedChunk;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentChunker {

    List<EmbeddedChunk> process(DocumentEntity document) {
        var sampleChunk = EmbeddedChunk.builder()
                .id(UUID.nameUUIDFromBytes("just some chunk".getBytes()))
                .documentId(document.getId())
                .documentTitle(document.getTitle())
                .content("Hello, I'm sample chunk content! I'm happy to see you!")
                .build();
        return List.of(sampleChunk);
    }
}
