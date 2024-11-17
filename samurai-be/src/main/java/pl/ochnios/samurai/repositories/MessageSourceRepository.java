package pl.ochnios.samurai.repositories;

import java.util.List;
import pl.ochnios.samurai.model.entities.conversation.MessageSource;

public interface MessageSourceRepository {

    Iterable<MessageSource> saveAll(List<MessageSource> sources);
}
