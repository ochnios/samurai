package pl.ochnios.ninjabe.model.mappers;

import org.mapstruct.Mapper;
import pl.ochnios.ninjabe.model.dtos.assistant.AssistantDto;
import pl.ochnios.ninjabe.model.entities.assistant.AssistantEntity;

@Mapper(componentModel = "spring")
public interface AssistantMapper {

    AssistantDto map(AssistantEntity assistantEntity);
}
