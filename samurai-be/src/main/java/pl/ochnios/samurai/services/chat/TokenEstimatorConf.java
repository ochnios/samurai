package pl.ochnios.samurai.services.chat;

import com.knuddels.jtokkit.api.EncodingType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tokenizer.JTokkitTokenCountEstimator;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TokenEstimatorConf {

    private static final EncodingType DEFAULT_ENCODING = EncodingType.CL100K_BASE;

    @Value("${custom.chat.tokenEncoding:cl100k_base}")
    private String tokenEncoding;

    @Bean
    public TokenCountEstimator tokenCountEstimator() {
        var encodingType = EncodingType.fromName(tokenEncoding);
        if (encodingType.isPresent()) {
            return new JTokkitTokenCountEstimator(encodingType.get());
        } else {
            log.warn("Can't find '{}' encoding type, using default {}", tokenEncoding, DEFAULT_ENCODING);
            return new JTokkitTokenCountEstimator(DEFAULT_ENCODING);
        }
    }
}
