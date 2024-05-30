package pl.ochnios.ninjabe.model.entities.conversation;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import pl.ochnios.ninjabe.model.entities.assistant.AssistantEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)
    private List<Message> messages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assistant_id", nullable = false)
    private AssistantEntity assistant;

    private UUID userId; // TODO user

    // TODO private Model model;

    @Column(length = 140)
    private String summary;

    @Column(nullable = false)
    private Boolean deleted;

    @CreationTimestamp
    private Instant createdAt;
}
