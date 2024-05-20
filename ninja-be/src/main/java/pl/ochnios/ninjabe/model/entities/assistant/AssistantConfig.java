package pl.ochnios.ninjabe.model.entities.assistant;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;
import pl.ochnios.ninjabe.model.dtos.assistant.AssistantVariant;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "assistant_configs")
public class AssistantConfig {

    @Id
    private UUID id;

    @MapsId("id")
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private AssistantEntity assistant;

    @Nationalized
    @Column(length = 4096)
    private String systemPrompt;

    @Column(nullable = false)
    private String chatModelName; // TODO separate Model entity

    @Column(nullable = false)
    private String embeddingModelName; // TODO separate Model entity

    private BigDecimal temperature;

    private Integer maxChatTokens;

    private Integer lastMessages;

    private String apiKey;

    @Enumerated(value = EnumType.STRING)
    private AssistantVariant variant;

    @UpdateTimestamp
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssistantConfig that = (AssistantConfig) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
