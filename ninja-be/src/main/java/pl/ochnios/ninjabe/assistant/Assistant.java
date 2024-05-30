package pl.ochnios.ninjabe.assistant;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.VectorStore;
import pl.ochnios.ninjabe.model.dtos.assistant.AssistantConfigDto;

public interface Assistant {

    ChatClient chat();

    EmbeddingClient embedding();

    VectorStore store();

    AssistantConfigDto config();
}
