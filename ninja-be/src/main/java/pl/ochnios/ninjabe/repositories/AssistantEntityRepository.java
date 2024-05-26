package pl.ochnios.ninjabe.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.ninjabe.model.entities.assistant.AssistantEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssistantEntityRepository extends CrudRepository<AssistantEntity, UUID> {

    List<AssistantEntity> findAllByEnabledIsTrueAndDeletedIsFalseOrderByNameAsc();
}
