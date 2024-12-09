package pl.ochnios.samurai.services.chat;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
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
    private final Map<UUID, String> documents = new HashMap<>();

    @Value("${custom.chat.maxDocumentTokens:16000}")
    private int docTokensLeft;

    @Value("${custom.chat.maxMessageTokens:2000}")
    private int msgTokensLeft;

    @PostConstruct
    public void init() {
        addMessage(Prompts.CHAT_PROMPT);
    }

    public boolean addDocument(UUID documentId, String content) {
        int tokens = estimator.estimate(content);
        if (docTokensLeft - tokens < 0) {
            return false;
        }

        documents.merge(documentId, content, (current, added) -> current + "\n\n" + added);
        docTokensLeft -= tokens;
        log.debug("Added {} context tokens, {} tokens left", tokens, docTokensLeft);

        return true;
    }

    public boolean canAddDocument(String content) {
        return docTokensLeft - estimator.estimate(content) >= 0;
    }

    public boolean addMessage(String content) {
        int tokens = estimator.estimate(content);
        if (msgTokensLeft - tokens < 0) {
            return false;
        }

        msgTokensLeft -= tokens;
        log.debug("Added {} message tokens, {} tokens left", tokens, msgTokensLeft);

        return true;
    }

    public boolean canAddMessage(String content) {
        return msgTokensLeft - estimator.estimate(content) >= 0;
    }
}
