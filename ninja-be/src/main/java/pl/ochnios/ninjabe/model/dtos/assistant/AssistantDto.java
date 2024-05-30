package pl.ochnios.ninjabe.model.dtos.assistant;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AssistantDto {

    private UUID id;
    private String ownerId;
    private String name;
    private String createdAt;
    private Boolean enabled;
    private Boolean deleted;
}
