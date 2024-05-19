package pl.ochnios.ninjabe.model.entities.assistant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "assistan_configs")
public class AssistantConfig {

    @Id
    @GeneratedValue
    UUID assistantConfigId;

}
