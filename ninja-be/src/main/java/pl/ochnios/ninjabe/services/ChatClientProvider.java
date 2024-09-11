package pl.ochnios.ninjabe.services;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.ChatOptionsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pl.ochnios.ninjabe.model.dtos.config.ChatOptionsDto;
import pl.ochnios.ninjabe.model.mappers.ChatOptionsMapper;

@Service
@RequiredArgsConstructor
public class ChatClientProvider {

    private final ChatOptionsMapper chatOptionsMapper;
    private final ChatModel chatModel;

    @Value("${custom.chat.model}")
    private String chatModelName;

    @Value("${custom.task.model}")
    private String taskModelName;

    public ChatClient getChatClient() {
        // TODO fetch chat options from db configuration
        final var chatOptionsDto =
                ChatOptionsDto.builder().model(chatModelName).temperature(0.7F).build();
        return getClient(chatOptionsDto);
    }

    public ChatClient getTaskClient() {
        // TODO fetch task options from db configuration
        final var chatOptionsDto =
                ChatOptionsDto.builder().model(taskModelName).temperature(0.7F).build();
        return getClient(chatOptionsDto);
    }

    public ChatClient getClient(ChatOptionsDto chatOptionsDto) {
        final ChatOptions chatOptions;
        if (chatOptionsDto == null) {
            chatOptions =
                    ChatOptionsBuilder.builder()
                            .withModel(chatModelName)
                            .withTemperature(0.7F)
                            .build();
        } else {
            chatOptions = chatOptionsMapper.map(chatOptionsDto);
        }
        return getClient(chatOptions);
    }

    private ChatClient getClient(ChatOptions chatOptions) {
        return ChatClient.builder(chatModel).defaultOptions(chatOptions).build();
    }
}
