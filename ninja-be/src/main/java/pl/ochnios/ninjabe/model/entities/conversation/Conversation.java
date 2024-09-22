package pl.ochnios.ninjabe.model.entities.conversation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import pl.ochnios.ninjabe.model.dtos.PatchDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationPatch;
import pl.ochnios.ninjabe.model.entities.PatchableEntity;
import pl.ochnios.ninjabe.model.entities.generator.CustomUuidGenerator;
import pl.ochnios.ninjabe.model.entities.user.User;

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
    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageEntity> messages = new ArrayList<>();

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 140)
    private String summary;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @Builder.Default
    @ColumnDefault(value = "0")
    private boolean deleted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conversation that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public PatchDto getPatchDto() {
        return new ConversationPatch(summary);
    }

    @Override
    public void apply(PatchDto patchDto) {
        final var conversationPatch = (ConversationPatch) patchDto;
        summary = conversationPatch.getSummary();
    }
}
