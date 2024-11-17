package pl.ochnios.samurai.services.chat.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.ochnios.samurai.model.dtos.config.ChatOptionsDto;

@Service
@RequiredArgsConstructor
public class ChatClientProvider {

    private final ChatModel chatModel;
    private final ChatOptionsFactory chatOptionsFactory;

    @Value("${custom.chat.model}")
    private String chatModelName;

    @Value("${custom.task.model}")
    private String taskModelName;

    public ChatClient getChatClient() {
        // TODO fetch chat options from db configuration
        var chatOptionsDto =
                ChatOptionsDto.builder().model(chatModelName).temperature(0.7).build();
        return getClient(chatOptionsDto);
    }

    public ChatClient getTaskClient() {
        // TODO fetch task options from db configuration
        var chatOptionsDto =
                ChatOptionsDto.builder().model(taskModelName).temperature(0.7).build();
        return getClient(chatOptionsDto);
    }

    public ChatClient getClient(ChatOptionsDto chatOptionsDto) {
        var chatOptions = chatOptionsFactory.get(chatOptionsDto, chatModel);
        return getClient(chatOptions);
    }

    private ChatClient getClient(ChatOptions chatOptions) {
        return ChatClient.builder(chatModel).defaultOptions(chatOptions).build();
    }
}
