package pl.ochnios.ninjabe.assistant;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class AssistantRegistry {

    private final Map<UUID, Assistant> assistants;

    public AssistantRegistry() {
        this.assistants = new HashMap<>();
    }

    public void register(UUID id, Assistant assistant) {
        try {
            assistants.put(id, assistant);
        } catch (Exception ex) {
            log.error("Failed to register assistant with id={}", id, ex);
            throw new AssistantRegistryException(ex.getMessage());
        }
        log.info("Registered assistant with id={}", id);
    }

    public Assistant get(UUID id) {
        Assistant assistant;
        try {
            assistant = assistants.get(id);
        } catch (Exception ex) {
            log.error("Failed to get assistant with id={}", id, ex);
            throw new AssistantRegistryException(ex.getMessage());
        }

        if (assistant == null) log.warn("Requested assistant not found, id={}", id);

        return assistant;
    }

    public void unregister(UUID id, Assistant assistant) {
        Assistant unregistered;
        try {
            unregistered = assistants.remove(id);
        } catch (Exception ex) {
            log.error("Failed to unregister assistant with id={}", id, ex);
            throw new AssistantRegistryException(ex.getMessage());
        }

        if (unregistered != null) log.info("Unregistered assistant with id={}", id);
        else log.warn("Can't find assistant to unregister, id={}", id);
    }

    public Assistant update(UUID id, Assistant assistant) {
        Assistant oldAssistant;
        try {
            oldAssistant = assistants.replace(id, assistant);
        } catch (Exception ex) {
            log.error("Failed to update assistant with id={}", id, ex);
            throw new AssistantRegistryException(ex.getMessage());
        }
        log.info("Updated assistant with id={}", id);

        return oldAssistant;
    }
}
