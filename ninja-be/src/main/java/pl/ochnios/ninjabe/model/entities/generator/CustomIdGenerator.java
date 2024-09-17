package pl.ochnios.ninjabe.model.entities.generator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.EventType;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.id.uuid.UuidGenerator;

import pl.ochnios.ninjabe.model.entities.AppEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

public class CustomIdGenerator extends UuidGenerator {
    public CustomIdGenerator(
            CustomUuidGenerator config,
            Member idMember,
            CustomIdGeneratorCreationContext creationContext) {
        super(getUuidGeneratorAnnotation(config.style()), idMember, creationContext);
    }

    private static org.hibernate.annotations.UuidGenerator getUuidGeneratorAnnotation(
            org.hibernate.annotations.UuidGenerator.Style style) {
        return new org.hibernate.annotations.UuidGenerator() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return org.hibernate.annotations.UuidGenerator.class;
            }

            @Override
            public Style style() {
                return style;
            }
        };
    }

    @Override
    public Object generate(
            SharedSessionContractImplementor session,
            Object owner,
            Object currentValue,
            EventType eventType) {

        if (owner instanceof AppEntity appEntity) {
            if (appEntity.getId() == null) {
                return super.generate(session, owner, currentValue, eventType);
            } else {
                return appEntity.getId();
            }
        }
        return super.generate(session, owner, currentValue, eventType);
    }
}
