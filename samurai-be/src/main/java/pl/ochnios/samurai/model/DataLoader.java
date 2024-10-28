package pl.ochnios.samurai.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.ochnios.samurai.model.seeders.BulkSeeder;
import pl.ochnios.samurai.model.seeders.ConversationSeeder;
import pl.ochnios.samurai.model.seeders.DocumentSeeder;
import pl.ochnios.samurai.model.seeders.UserSeeder;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements ApplicationRunner {

    private final UserSeeder userSeeder;
    private final ConversationSeeder conversationSeeder;
    private final DocumentSeeder documentSeeder;
    private final BulkSeeder bulkSeeder;

    @Value("${custom.loader.enabled}")
    private Boolean loaderEnabled;

    @Value("${custom.loader.bulk}")
    private Boolean bulkEnabled;

    @Override
    public void run(ApplicationArguments args) {
        if (loaderEnabled) {
            log.info("Started loading data");
            userSeeder.seed();
            conversationSeeder.seed();
            documentSeeder.seed();
            if (bulkEnabled) {
                bulkSeeder.seed();
            }
            log.info("Finished loading data");
        }
    }
}
