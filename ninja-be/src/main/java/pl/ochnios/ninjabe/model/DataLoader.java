package pl.ochnios.ninjabe.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import pl.ochnios.ninjabe.model.seeders.UserSeeder;

@Profile({"local", "dev"})
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements ApplicationRunner {

    private final UserSeeder userSeeder;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Started loading data");
        userSeeder.seed();
        log.info("Finished loading data");
    }
}
