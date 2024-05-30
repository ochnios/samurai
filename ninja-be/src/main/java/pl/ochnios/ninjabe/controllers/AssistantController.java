package pl.ochnios.ninjabe.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.ninjabe.model.dtos.assistant.AssistantDto;
import pl.ochnios.ninjabe.services.AssistantEntityService;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantEntityService assistantEntityService;

    @GetMapping("/assistants/available")
    public List<AssistantDto> getAvailableAssistants() {
        return assistantEntityService.getAvailableAssistants();
    }
}
