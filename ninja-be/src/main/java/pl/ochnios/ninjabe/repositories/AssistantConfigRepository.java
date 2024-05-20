package pl.ochnios.ninjabe.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.ninjabe.model.entities.assistant.AssistantConfig;

import java.util.UUID;

@Repository
public interface AssistantConfigRepository extends CrudRepository<AssistantConfig, UUID> {

    Iterable<AssistantConfig> findAllByAssistant_EnabledAndAssistant_Deleted(Boolean enabled, Boolean deleted);
}
