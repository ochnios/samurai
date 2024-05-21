package pl.ochnios.ninjabe.model.seeders;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.ochnios.ninjabe.model.dtos.assistant.AssistantVariant;
import pl.ochnios.ninjabe.model.entities.assistant.AssistantConfig;
import pl.ochnios.ninjabe.model.entities.assistant.AssistantEntity;
import pl.ochnios.ninjabe.repositories.AssistantConfigRepository;
import pl.ochnios.ninjabe.repositories.AssistantEntityRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssistantSeeder implements DataSeeder {

    private final AssistantEntityRepository assistantEntityRepository;
    private final AssistantConfigRepository assistantConfigRepository;

    @Value("${custom.default-api-key}")
    private String defaultApiKey;

    @Override
    @Transactional
    public void seed() {
        var assistant = assistantEntityRepository.save(generateBasicAssistant());
        var config = assistantConfigRepository.save(generateBasicConfig(assistant.getId()));
        assistant.setConfig(config);
        assistantEntityRepository.save(assistant);
    }

    private AssistantEntity generateBasicAssistant() {
        return AssistantEntity.builder()
                .name("First Ninja")
                .enabled(true)
                .deleted(false)
                .build();
    }

    private AssistantConfig generateBasicConfig(UUID assistantId) {
        return AssistantConfig.builder()
                .id(assistantId)
                .systemPrompt("You are a helpful assistant")
                .chatModelName("gpt-3.5-turbo")
                .embeddingModelName("text-embedding-small")
                .temperature(BigDecimal.ONE)
                .maxChatTokens(4096)
                .lastMessages(10)
                .apiKey(defaultApiKey)
                .variant(AssistantVariant.Basic)
                .build();
    }
}
