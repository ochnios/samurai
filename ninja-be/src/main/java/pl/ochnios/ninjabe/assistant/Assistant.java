package pl.ochnios.ninjabe.assistant;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.VectorStore;

public interface Assistant {

    ChatClient chat();

    EmbeddingClient embedding();

    VectorStore store();
}
