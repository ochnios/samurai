package pl.ochnios.ninjabe.model.dtos.assistant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssistantDto {

    private String assistantId;
    private String ownerId;
    private String name;
    private String createdAt;
    private Boolean enabled;
    private Boolean deleted;
}
