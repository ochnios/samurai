package pl.ochnios.ninjabe.model.entities.chat;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.MessageType;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message implements org.springframework.ai.chat.messages.Message {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Lob
    @Nationalized
    private String content;

    private Integer tokensIn;

    private Integer tokensOut;

    @Enumerated(value = EnumType.STRING)
    private MessageType type;

    @CreationTimestamp
    private Instant createdAt;

    public static Message message(Conversation conversation, String content, MessageType type) {
        return Message.builder()
                .conversation(conversation)
                .content(content)
                .type(type)
                .build();
    }

    public static Message user(Conversation conversation, String content) {
        return Message.message(conversation, content, MessageType.USER);
    }

    public static Message system(Conversation conversation, String content) {
        return Message.message(conversation, content, MessageType.SYSTEM);
    }

    public static Message assistantt(Conversation conversation, String content) {
        return Message.message(conversation, content, MessageType.ASSISTANT);
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public List<Media> getMedia() {
        return null;
    }

    @Override
    public Map<String, Object> getProperties() {
        return null;
    }

    @Override
    public MessageType getMessageType() {
        return type;
    }
}
