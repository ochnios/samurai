package pl.ochnios.samurai.repositories.impl;

import org.springframework.data.repository.CrudRepository;
import pl.ochnios.samurai.model.entities.conversation.MessageSource;

import java.util.UUID;

public interface MessageSourceCrudRepository extends CrudRepository<MessageSource, UUID> {}
