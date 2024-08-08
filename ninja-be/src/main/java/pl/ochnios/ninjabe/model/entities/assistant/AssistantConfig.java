package pl.ochnios.ninjabe.model.entities.assistant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Id private UUID id;

    @MapsId("id")
    @OneToOne(
            cascade = {CascadeType.MERGE, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    private AssistantEntity assistant;

    @Lob @Nationalized private String systemPrompt;

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

    @UpdateTimestamp private Instant updatedAt;

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
