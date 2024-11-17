package pl.ochnios.samurai.services.chat.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;
import pl.ochnios.samurai.model.dtos.config.ChatOptionsDto;
import pl.ochnios.samurai.model.mappers.ChatOptionsMapper;

@Component
@RequiredArgsConstructor
public class ChatOptionsFactory {

    private final ChatOptionsMapper chatOptionsMapper;

    public ChatOptions get(ChatOptionsDto chatOptionsDto, ChatModel chatModel) {
        if (chatModel instanceof OpenAiChatModel) {
            return chatOptionsMapper.mapOpenAiChatOptions(chatOptionsDto);
        } else if (chatModel instanceof AnthropicChatModel) {
            return chatOptionsMapper.mapAnthropicChatOptions(chatOptionsDto);
        } else {
            return chatOptionsMapper.mapDefaultChatOptions(chatOptionsDto);
        }
    }
}
