package pl.ochnios.ninjabe.model.entities.assistant;

import jakarta.persistence.*;
import lombok.Getter;
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
