package pl.ochnios.samurai.model.mappers;

import org.mapstruct.Mapper;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.ChatOptionsBuilder;
import pl.ochnios.samurai.model.dtos.config.ChatOptionsDto;

@Mapper
public interface ChatOptionsMapper {

    default ChatOptions map(ChatOptionsDto chatOptionsDto) {
        if (chatOptionsDto == null)
            return null;
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
}
