package pl.ochnios.samurai.model.mappers;

import org.mapstruct.Mapper;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.ChatOptionsBuilder;
import org.springframework.ai.openai.OpenAiChatOptions;
import pl.ochnios.samurai.model.dtos.config.ChatOptionsDto;

@Mapper
public interface ChatOptionsMapper {

    default ChatOptions mapDefaultChatOptions(ChatOptionsDto chatOptionsDto) {
        if (chatOptionsDto == null) return null;
        return ChatOptionsBuilder.builder()
                .withModel(chatOptionsDto.getModel())
                .withFrequencyPenalty(chatOptionsDto.getFrequencyPenalty())
                .withMaxTokens(chatOptionsDto.getMaxTokens())
                .withPresencePenalty(chatOptionsDto.getPresencePenalty())
                .withTemperature(chatOptionsDto.getTemperature())
                .withTopK(chatOptionsDto.getTopK())
                .withTopP(chatOptionsDto.getTopP())
                .build();
    }

    default ChatOptions mapOpenAiChatOptions(ChatOptionsDto chatOptionsDto) {
        if (chatOptionsDto == null) return null;
        return OpenAiChatOptions.builder()
                .withModel(chatOptionsDto.getModel())
                .withFrequencyPenalty(chatOptionsDto.getFrequencyPenalty())
                .withMaxTokens(chatOptionsDto.getMaxTokens())
                .withPresencePenalty(chatOptionsDto.getPresencePenalty())
                .withTemperature(chatOptionsDto.getTemperature())
                .withTopP(chatOptionsDto.getTopP())
                .build();
    }

    default ChatOptions mapAnthropicChatOptions(ChatOptionsDto chatOptionsDto) {
        if (chatOptionsDto == null) return null;
        return AnthropicChatOptions.builder()
                .withModel(chatOptionsDto.getModel())
                .withMaxTokens(chatOptionsDto.getMaxTokens())
                .withTemperature(chatOptionsDto.getTemperature())
                .withTopK(chatOptionsDto.getTopK())
                .withTopP(chatOptionsDto.getTopP())
                .build();
    }
}
