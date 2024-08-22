package pl.ochnios.ninjabe.services.ai;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatClientProvider {

    private final ChatModel chatModel;

    @Value("${custom.chat.model}")
    private String chatModelName;

    @Value("${custom.task.model}")
    private String taskModelName;

    public ChatClient getChatClient() {
        return getChatClient(new ChatConfiguration());
    }

    public ChatClient getTaskClient() {
        return getTaskClient(new ChatConfiguration());
    }

    public ChatClient getChatClient(ChatConfiguration configuration) {
        configuration.setModel(chatModelName);
        return getClient(configuration);
    }

    public ChatClient getTaskClient(ChatConfiguration configuration) {
        configuration.setModel(taskModelName);
        return getClient(configuration);
    }

    private ChatClient getClient(ChatConfiguration configuration) {
        final var options = applyConfiguration(configuration);
        return ChatClient.builder(chatModel).defaultOptions(options).build();
    }

    private ChatOptions applyConfiguration(ChatConfiguration config) {
        final var defaultOptions = chatModel.getDefaultOptions();
        if (config == null || config.isUseDefault()) return defaultOptions;
        if (defaultOptions instanceof OpenAiChatOptions options) {
            return applyOpenAiConfiguration(options, config);
        } else if (defaultOptions instanceof AnthropicChatOptions options) {
            return applyAnthropicConfiguration(options, config);
        }
        return defaultOptions;
    }

    private OpenAiChatOptions applyOpenAiConfiguration(
            OpenAiChatOptions defaultOptions, ChatConfiguration config) {
        final var options = defaultOptions.copy();
        if (config.getModel() != null) options.setModel(config.getModel());
        if (config.getMaxTokens() != null) options.setMaxTokens(config.getMaxTokens());
        if (config.getPresencePenalty() != null) options.setMaxTokens(config.getMaxTokens());
        if (config.getTemperature() != null) options.setTemperature(config.getTemperature());
        if (config.getTopP() != null) options.setTopP(config.getTopP());
        if (config.getFrequencyPenalty() != null)
            options.setFrequencyPenalty(config.getFrequencyPenalty());
        return options;
    }

    private AnthropicChatOptions applyAnthropicConfiguration(
            AnthropicChatOptions defaultOptions, ChatConfiguration config) {
        final var options = defaultOptions.copy();
        if (config.getModel() != null) options.setModel(config.getModel());
        if (config.getMaxTokens() != null) options.setMaxTokens(config.getMaxTokens());
        if (config.getPresencePenalty() != null) options.setMaxTokens(config.getMaxTokens());
        if (config.getTemperature() != null) options.setTemperature(config.getTemperature());
        if (config.getTopK() != null) options.setTopK(config.getTopK());
        if (config.getTopP() != null) options.setTopP(config.getTopP());
        return options;
    }
}
