package pl.ochnios.ninjabe.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import pl.ochnios.ninjabe.model.dtos.assistant.AssistantDto;
import pl.ochnios.ninjabe.model.mappers.AssistantMapper;
import pl.ochnios.ninjabe.repositories.AssistantEntityRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssistantEntityService {

    private final AssistantEntityRepository assistantRepository;
    private final AssistantMapper assistantMapper;

    public List<AssistantDto> getAvailableAssistants() {
        return assistantRepository.findAllByEnabledIsTrueAndDeletedIsFalseOrderByNameAsc().stream()
                .map(assistantMapper::map)
                .toList();
    }
}
