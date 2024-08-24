package pl.ochnios.ninjabe.services.ai;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatConfiguration {

    private String model;
    private Float frequencyPenalty;
    private Integer maxTokens;
    private Float presencePenalty;
    private Float temperature;
    private Integer topK;
    private Float topP;
    private boolean useDefault;
}
