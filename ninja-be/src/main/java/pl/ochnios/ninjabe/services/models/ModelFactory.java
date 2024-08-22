package pl.ochnios.ninjabe.services.models;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingOptions;

public interface ModelFactory {

    ChatModel createChatModel(Object api, ChatOptions options);

    EmbeddingModel createEmbeddingModel(Object api, EmbeddingOptions options);
}
