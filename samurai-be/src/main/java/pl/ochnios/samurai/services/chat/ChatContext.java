package pl.ochnios.samurai.services.chat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
@Component
@RequiredArgsConstructor
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ChatContext {

    private final TokenCountEstimator estimator;

    @Getter
    private final Set<UUID> documents = new HashSet<>();

    @Value("${custom.chat.maxContextTokens:16000}")
    private int availableTokens;

    public boolean add(UUID documentId, String content) {
        int tokens = estimator.estimate(content);
        if (availableTokens - tokens < 0) {
            return false;
        }

        documents.add(documentId);
        availableTokens -= tokens;
        log.debug("Added {} tokens to context, {} tokens left", tokens, availableTokens);

        return true;
    }

    public boolean canBeAdded(String content) {
        return availableTokens - estimator.estimate(content) >= 0;
    }
}
