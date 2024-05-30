package pl.ochnios.ninjabe.model.mappers;

import org.mapstruct.Mapper;
import pl.ochnios.ninjabe.model.dtos.assistant.AssistantConfigDto;
import pl.ochnios.ninjabe.model.entities.assistant.AssistantConfig;

@Mapper(componentModel = "spring")
public interface AssistantConfigMapper {

    AssistantConfigDto map(AssistantConfig assistantConfig);
}
