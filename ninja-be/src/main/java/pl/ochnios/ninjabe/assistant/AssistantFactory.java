package pl.ochnios.ninjabe.assistant;

import io.qdrant.client.QdrantClient;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
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
            default -> {
                return null;
            }
        }
    }

    private BasicAssistant createBasicAssistant(AssistantConfigDto config) {
        var openAiApi = new OpenAiApi(config.getApiKey());
        var chatModel = createOpenAiChatModel(openAiApi, config);
        var embeddingModel = createOpenAiEmbeddingModel(openAiApi, config);
        var vectorStore = createQdrantVectorStore(embeddingModel, config.getId().toString());
        return new BasicAssistant(chatModel, embeddingModel, vectorStore, config);
    }

    private OpenAiChatModel createOpenAiChatModel(OpenAiApi openAiApi, AssistantConfigDto config) {
        var openAiChatOptions =
                OpenAiChatOptions.builder()
                        .withModel(config.getChatModelName())
                        .withTemperature(config.getTemperature())
                        .withMaxTokens(config.getMaxChatTokens())
                        .build();
        return new OpenAiChatModel(openAiApi, openAiChatOptions);
    }

    private OpenAiEmbeddingModel createOpenAiEmbeddingModel(
            OpenAiApi openAiApi, AssistantConfigDto config) {
        var openAiEmbeddingOptions =
                OpenAiEmbeddingOptions.builder().withModel(config.getEmbeddingModelName()).build();
        return new OpenAiEmbeddingModel(
                openAiApi,
                MetadataMode.EMBED,
                openAiEmbeddingOptions,
                RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    private QdrantVectorStore createQdrantVectorStore(
            OpenAiEmbeddingModel embeddingClient, String collectionName) {
        return new QdrantVectorStore(qdrantClient, collectionName, embeddingClient, true);
    }
}
