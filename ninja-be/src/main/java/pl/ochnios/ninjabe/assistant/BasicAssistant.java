package pl.ochnios.ninjabe.assistant;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import pl.ochnios.ninjabe.model.dtos.assistant.AssistantConfigDto;

@RequiredArgsConstructor
public class BasicAssistant implements Assistant {

    private final OpenAiChatModel chatModel;
    private final OpenAiEmbeddingModel embeddingModel;
    private final QdrantVectorStore vectorStore;
    private final AssistantConfigDto assistantConfigDto;

    @Override
    public ChatModel chat() {
        return chatModel;
    }

    @Override
    public EmbeddingModel embedding() {
        return embeddingModel;
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
