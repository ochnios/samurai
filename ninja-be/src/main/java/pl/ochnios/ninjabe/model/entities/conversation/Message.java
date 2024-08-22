package pl.ochnios.ninjabe.model.entities.conversation;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.springframework.ai.chat.messages.MessageType;

import java.time.Instant;
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

    @Id @GeneratedValue private UUID id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Lob @Nationalized private String content;

    private Integer tokensIn;

    private Integer tokensOut;

    @Enumerated(value = EnumType.STRING)
    private MessageType type;

    @CreationTimestamp private Instant createdAt;

    public static Message message(Conversation conversation, String content, MessageType type) {
        return Message.builder().conversation(conversation).content(content).type(type).build();
    }

    public static Message user(Conversation conversation, String content) {
        return Message.message(conversation, content, MessageType.USER);
    }

    public static Message system(Conversation conversation, String content) {
        return Message.message(conversation, content, MessageType.SYSTEM);
    }

    public static Message assistant(Conversation conversation, String content) {
        return Message.message(conversation, content, MessageType.ASSISTANT);
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public MessageType getMessageType() {
        return type;
    }
}
