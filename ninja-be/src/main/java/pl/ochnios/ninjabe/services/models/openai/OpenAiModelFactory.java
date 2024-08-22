package pl.ochnios.ninjabe.services.models.openai;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.stereotype.Component;

import pl.ochnios.ninjabe.services.models.ModelFactory;

@Component
@RequiredArgsConstructor
public class OpenAiModelFactory implements ModelFactory {

    private OpenAiChatModel chatModel;

    @Override
    public OpenAiChatModel createChatModel(Object api, ChatOptions options) {
        return null;
    }

    @Override
    public OpenAiEmbeddingModel createEmbeddingModel(Object api, EmbeddingOptions options) {
        return null;
    }
}
