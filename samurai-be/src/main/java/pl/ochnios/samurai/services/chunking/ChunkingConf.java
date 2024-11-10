package pl.ochnios.samurai.services.chunking;

import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChunkingConf {

    @Bean
    public TextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }
}
