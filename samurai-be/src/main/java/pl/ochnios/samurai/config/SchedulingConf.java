package pl.ochnios.samurai.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.ochnios.samurai.services.chunking.DocumentChunkingService;
import pl.ochnios.samurai.services.chunking.DocumentChunkingTask;

@Configuration
@EnableScheduling
public class SchedulingConf {

    @Bean
    @ConditionalOnProperty(prefix = "custom.chunking", name = "enabled")
    public DocumentChunkingTask documentChunkingTask(DocumentChunkingService chunkingService) {
        return new DocumentChunkingTask(chunkingService);
    }
}
