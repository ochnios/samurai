package pl.ochnios.samurai.model.entities.conversation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.mapstruct.factory.Mappers;
import pl.ochnios.samurai.model.dtos.PatchDto;
import pl.ochnios.samurai.model.dtos.conversation.ConversationDto;
import pl.ochnios.samurai.model.entities.PatchableEntity;
import pl.ochnios.samurai.model.entities.generator.CustomUuidGenerator;
import pl.ochnios.samurai.model.entities.user.User;
import pl.ochnios.samurai.model.mappers.ConversationMapper;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "conversations")
public class Conversation implements PatchableEntity {

    @Id
    @CustomUuidGenerator
    private UUID id;

    @Builder.Default
    @ToString.Exclude
    @OrderBy("createdAt asc")
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MessageEntity> messages = new ArrayList<>();

    @Builder.Default
    @Setter(AccessLevel.NONE)
    private int messageCount = 0;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 140)
    private String summary;

    @Builder.Default
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    @Builder.Default
    @ColumnDefault(value = "0")
    private boolean deleted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Conversation that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public PatchDto getPatchDto() {
        final var conversationMapper = Mappers.getMapper(ConversationMapper.class);
        return conversationMapper.map(this);
    }

    @Override
    public void apply(PatchDto patchDto) {
        final var conversationPatchDto = (ConversationDto) patchDto;
        summary = conversationPatchDto.getSummary();
    }

    public void setMessages(List<MessageEntity> messages) {
        this.messages = messages;
        this.messageCount = messages.size();
    }

    public void addMessages(List<MessageEntity> messages) {
        this.messages.addAll(messages);
        this.messageCount = this.messages.size();
    }

    public void addMessage(MessageEntity messageEntity) {
        this.messages.add(messageEntity);
        this.messageCount = this.messages.size();
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void updateMessageCount() {
        messageCount = messages != null ? messages.size() : 0;
    }
}
