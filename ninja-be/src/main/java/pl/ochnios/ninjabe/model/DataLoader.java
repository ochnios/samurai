package pl.ochnios.ninjabe.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.ochnios.ninjabe.model.seeders.AssistantSeeder;

@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final AssistantSeeder assistantSeeder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Started loading data");
        assistantSeeder.seed();
        log.info("Finished loading data");
    }
}
