package pl.ochnios.ninjabe.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ochnios.ninjabe.model.dtos.assistant.AssistantConfigDto;
import pl.ochnios.ninjabe.model.mappers.AssistantConfigMapper;
import pl.ochnios.ninjabe.repositories.AssistantConfigRepository;

import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AssistantConfigService {

    private final AssistantConfigRepository assistantConfigRepository;
    private final AssistantConfigMapper assistantConfigMapper;

    public Iterable<AssistantConfigDto> findConfigsForActiveAssistants() {
        var configEntities = assistantConfigRepository
                .findAllByAssistant_EnabledAndAssistant_Deleted(true, false);
        return StreamSupport.stream(configEntities.spliterator(), false)
                .map(assistantConfigMapper::map).toList();
    }
}
