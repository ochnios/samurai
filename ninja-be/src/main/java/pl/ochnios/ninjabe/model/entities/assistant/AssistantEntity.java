package pl.ochnios.ninjabe.model.entities.assistant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "assistants")
public class AssistantEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(mappedBy = "assistant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private AssistantConfig config;

    @CreationTimestamp
    private Instant createdAt;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private Boolean deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssistantEntity that = (AssistantEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
