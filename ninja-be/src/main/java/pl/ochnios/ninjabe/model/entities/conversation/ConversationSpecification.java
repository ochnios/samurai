package pl.ochnios.ninjabe.model.entities.conversation;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationCriteria;

public class ConversationSpecification {

    public static Specification<Conversation> create(ConversationCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getGlobalSearch() != null) {
                String globalSearchPattern = "%" + criteria.getGlobalSearch().toLowerCase() + "%";
                final var summaryPredicate = builder.like(builder.lower(root.get("summary")), globalSearchPattern);
                String fullNamePattern = "%" + criteria.getGlobalSearch().toLowerCase() + "%";
                final var fullNamePredicate = builder.like(
                        builder.lower(builder.concat(
                                builder.concat(root.join("user").get("lastname"), " "),
                                root.join("user").get("firstname"))),
                        fullNamePattern);
                predicates.add(builder.or(summaryPredicate, fullNamePredicate));
            }

            if (criteria.getMinMessageCount() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("messageCount"), criteria.getMinMessageCount()));
            }

            if (criteria.getMaxMessageCount() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("messageCount"), criteria.getMaxMessageCount()));
            }

            if (criteria.getMinCreatedAt() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"), criteria.getMinCreatedAt()));
            }

            if (criteria.getMaxCreatedAt() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("createdAt"), criteria.getMaxCreatedAt()));
            }

            if (criteria.getSummary() != null) {
                predicates.add(builder.like(
                        builder.lower(root.get("summary")),
                        "%" + criteria.getSummary().toLowerCase() + "%"));
            }

            if (criteria.getUserFullName() != null) {
                String fullNamePattern = "%" + criteria.getUserFullName().toLowerCase() + "%";
                final var fullNamePredicate = builder.like(
                        builder.lower(builder.concat(
                                builder.concat(root.join("user").get("lastname"), " "),
                                root.join("user").get("firstname"))),
                        fullNamePattern);
                predicates.add(fullNamePredicate);
            }

            if (criteria.getDeleted() != null) {
                predicates.add(builder.equal(root.get("deleted"), criteria.getDeleted()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
