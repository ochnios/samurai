package pl.ochnios.ninjabe.model.dtos.assistant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssistantConfigDto {

    private String assistantId;
    private String chatModelName;
    private String embeddingModelName;
    private String systemPrompt;
    private Float temperature;
    private Integer maxChatTokens;
    private Integer lastMessages;
    private String apiKey;
    private AssistantVariant variant;
}
