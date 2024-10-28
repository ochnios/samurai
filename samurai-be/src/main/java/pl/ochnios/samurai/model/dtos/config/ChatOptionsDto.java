package pl.ochnios.samurai.model.dtos.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatOptionsDto {

    private String model;
    private Float frequencyPenalty;
    private Integer maxTokens;
    private Float presencePenalty;
    private Float temperature;
    private Integer topK;
    private Float topP;
}
