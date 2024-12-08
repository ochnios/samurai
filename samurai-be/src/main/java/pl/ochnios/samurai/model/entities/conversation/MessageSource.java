package pl.ochnios.samurai.model.entities.conversation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import pl.ochnios.samurai.model.entities.AppEntity;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.generator.CustomUuidGenerator;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "message_sources")
public class MessageSource implements AppEntity {

    @Id
    @CustomUuidGenerator
    private UUID id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private MessageEntity message;

    @Column(updatable = false, nullable = false)
    private String originalTitle;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    @Nationalized
    @Column(length = Integer.MAX_VALUE)
    private String retrievedContent;

    @Builder.Default
    @CreationTimestamp
    @Column(updatable = false)
    private Instant accessedAt = Instant.now();

    @Builder.Default
    @Setter(AccessLevel.NONE)
    private boolean documentDeleted = false;

    @Builder.Default
    private boolean deleted = false;

    public void detachDocument() {
        document = null;
        documentDeleted = true;
    }
}
