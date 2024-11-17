package pl.ochnios.samurai.repositories.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.ochnios.samurai.model.entities.conversation.MessageSource;
import pl.ochnios.samurai.repositories.MessageSourceRepository;

@Repository
@RequiredArgsConstructor
public class MessageSourceRepositoryImpl implements MessageSourceRepository {

    private final MessageSourceCrudRepository messageSourceCrudRepository;

    @Override
    public Iterable<MessageSource> saveAll(List<MessageSource> sources) {
        return messageSourceCrudRepository.saveAll(sources);
    }
}
