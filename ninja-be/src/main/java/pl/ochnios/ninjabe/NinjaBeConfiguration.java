package pl.ochnios.ninjabe;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.ochnios.ninjabe.assistant.AssistantFactory;
import pl.ochnios.ninjabe.assistant.AssistantRegistry;
import pl.ochnios.ninjabe.model.dtos.assistant.AssistantConfigDto;
import pl.ochnios.ninjabe.model.dtos.assistant.AssistantVariant;

import java.util.UUID;

@Configuration
public class NinjaBeConfiguration {

    @Value("${custom.qdrant.client.host}")
    private String qdrantClientHost;

    @Value("${custom.qdrant.client.port}")
    private int qdrantClientPort;

    @Value("${custom.qdrant.client.tls}")
    private boolean qdrantClientTls;

    @Value("${custom.default-api-key}")
    private String defaultApiKey;

    @Bean
    public QdrantClient qdrantClient() {
        return new QdrantClient(
                QdrantGrpcClient.newBuilder(qdrantClientHost, qdrantClientPort, qdrantClientTls).build()
        );
    }

    @Bean
    ApplicationRunner registerAssistants(AssistantFactory assistantFactory, AssistantRegistry assistantRegistry) {
        return args -> {
            // TODO initializing assistants basing on config from database
            var dummyAssistantConfig = dummyAssistantConfig();
            var dummyAssistant = assistantFactory.createAssistant(dummyAssistantConfig);
            var id = UUID.fromString(dummyAssistantConfig.getId());
            assistantRegistry.register(id, dummyAssistant);
        };
    }

    private AssistantConfigDto dummyAssistantConfig() {
        return AssistantConfigDto.builder()
                .id(UUID.nameUUIDFromBytes("dummy".getBytes()).toString())
                .chatModelName("gpt-3.5-turbo")
                .embeddingModelName("text-embedding-small")
                .systemPrompt("You are a helpful assistant")
                .temperature(1.0f)
                .maxChatTokens(4096)
                .lastMessages(10)
                .apiKey(defaultApiKey)
                .variant(AssistantVariant.Basic)
                .build();
    }
}
