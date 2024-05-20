package pl.ochnios.ninjabe.assistant;

import io.qdrant.client.QdrantClient;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.stereotype.Component;
import pl.ochnios.ninjabe.model.dtos.assistant.AssistantConfigDto;

@Component
@RequiredArgsConstructor
public class AssistantFactory {

    private final QdrantClient qdrantClient;

    public Assistant createAssistant(AssistantConfigDto config) {
        switch (config.getVariant()) {
            case Basic -> {
                return createBasicAssistant(config);
            }
            default -> throw new RuntimeException("Unsupported assistant variant");
        }
    }

    private BasicAssistant createBasicAssistant(AssistantConfigDto config) {
        var openAiApi = new OpenAiApi(config.getApiKey());
        var chatClient = createOpenAiChatClient(openAiApi, config);
        var embeddingClient = createOpenAiEmbeddingClient(openAiApi, config);
        var vectorStore = createQdrantVectorStore(embeddingClient, config.getId());
        return new BasicAssistant(chatClient, embeddingClient, vectorStore);
    }

    private OpenAiChatClient createOpenAiChatClient(OpenAiApi openAiApi, AssistantConfigDto config) {
        var openAiChatOptions = OpenAiChatOptions.builder()
                .withModel(config.getChatModelName())
                .withTemperature(config.getTemperature())
                .withMaxTokens(config.getMaxChatTokens())
                .build();
        return new OpenAiChatClient(openAiApi, openAiChatOptions);
    }

    private OpenAiEmbeddingClient createOpenAiEmbeddingClient(OpenAiApi openAiApi, AssistantConfigDto config) {
        var openAiEmbeddingOptions = OpenAiEmbeddingOptions.builder()
                .withModel(config.getEmbeddingModelName())
                .build();
        return new OpenAiEmbeddingClient(openAiApi, MetadataMode.EMBED,
                openAiEmbeddingOptions, RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    private QdrantVectorStore createQdrantVectorStore(EmbeddingClient embeddingClient, String collectionName) {
        return new QdrantVectorStore(qdrantClient, collectionName, embeddingClient);
    }
}

