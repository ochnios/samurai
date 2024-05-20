package pl.ochnios.ninjabe.model.dtos.assistant;

import org.mapstruct.Mapper;
import pl.ochnios.ninjabe.model.entities.assistant.AssistantConfig;

@Mapper(componentModel = "spring")
public interface AssistantConfigMapper {

    AssistantConfigDto map(AssistantConfig assistantConfig);
}
