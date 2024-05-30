package pl.ochnios.ninjabe.assistant;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import pl.ochnios.ninjabe.model.dtos.assistant.AssistantConfigDto;

@RequiredArgsConstructor
public class BasicAssistant implements Assistant {
    
    private final OpenAiChatClient chatClient;
    private final OpenAiEmbeddingClient embeddingClient;
    private final QdrantVectorStore vectorStore;
    private final AssistantConfigDto assistantConfigDto;

    @Override
    public ChatClient chat() {
        return chatClient;
    }

    @Override
    public EmbeddingClient embedding() {
        return embeddingClient;
    }

    @Override
    public VectorStore store() {
        return vectorStore;
    }

    @Override
    public AssistantConfigDto config() {
        return assistantConfigDto;
    }
}
