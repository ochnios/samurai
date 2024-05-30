package pl.ochnios.ninjabe.assistant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.ochnios.ninjabe.services.AssistantConfigService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssistantLoader implements ApplicationListener<ApplicationReadyEvent> {

    private final AssistantFactory assistantFactory;
    private final AssistantRegistry assistantRegistry;
    private final AssistantConfigService assistantConfigService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        var configs = assistantConfigService.findConfigsForActiveAssistants();
        for (var config : configs) {
            var assistant = assistantFactory.createAssistant(config);
            try {
                assistantRegistry.register(config.getId(), assistant);
            } catch (AssistantRegistryException ex) {
                // TODO Notify assistant owner or sth
            }
        }
    }
}
