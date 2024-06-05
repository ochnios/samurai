package pl.ochnios.ninjabe.assistant;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import pl.ochnios.ninjabe.model.dtos.assistant.AssistantConfigDto;

public interface Assistant {

    ChatModel chat();

    EmbeddingModel embedding();

    VectorStore store();

    AssistantConfigDto config();
}
