package pl.ochnios.samurai.model.entities.conversation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.springframework.ai.chat.messages.MessageType;
import pl.ochnios.samurai.model.entities.AppEntity;
import pl.ochnios.samurai.model.entities.generator.CustomUuidGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "messages")
public class MessageEntity implements AppEntity {

    @Id
    @CustomUuidGenerator
    private UUID id;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MessageSource> sources = new ArrayList<>();

    @Nationalized
    @Column(length = 16384)
    private String content;

    @Enumerated(value = EnumType.STRING)
    private MessageType type;

    @Builder.Default
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt = Instant.now();

    @Builder.Default
    @ColumnDefault(value = "0")
    private boolean deleted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
