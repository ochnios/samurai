package pl.ochnios.samurai.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.ochnios.samurai.model.seeders.BulkSeeder;
import pl.ochnios.samurai.model.seeders.ChunkSeeder;
import pl.ochnios.samurai.model.seeders.ConversationSeeder;
import pl.ochnios.samurai.model.seeders.DocumentSeeder;
import pl.ochnios.samurai.model.seeders.UserSeeder;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "custom.loader", name = "enabled")
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final UserSeeder userSeeder;
    private final ConversationSeeder conversationSeeder;
    private final DocumentSeeder documentSeeder;
    private final ChunkSeeder chunkSeeder;
    private final BulkSeeder bulkSeeder;

    @Value("${custom.loader.bulk:false}")
    private Boolean bulkEnabled;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Started loading data");
        userSeeder.seed();
        conversationSeeder.seed();
        documentSeeder.seed();
        chunkSeeder.seed();
        if (bulkEnabled) {
            bulkSeeder.seed();
        }
        log.info("Finished loading data");
    }
}
