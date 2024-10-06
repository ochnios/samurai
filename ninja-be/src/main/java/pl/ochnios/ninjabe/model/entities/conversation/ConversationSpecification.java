package pl.ochnios.ninjabe.model.entities.conversation;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationCriteria;

import java.util.ArrayList;
import java.util.List;

public class ConversationSpecification {

    public static Specification<Conversation> create(ConversationCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getGlobalSearch() != null) {
                String globalSearch = "%" + criteria.getGlobalSearch().toLowerCase() + "%";
                final var summaryPredicate = builder.like(builder.lower(root.get("summary")), globalSearch);
                final var firstNamePredicate =
                        builder.like(builder.lower(root.join("user").get("firstname")), globalSearch);
                final var lastNamePredicate =
                        builder.like(builder.lower(root.join("user").get("lastname")), globalSearch);
                predicates.add(builder.or(summaryPredicate, firstNamePredicate, lastNamePredicate));
            }

            if (criteria.getMinMessageCount() != null) {
                predicates.add(builder.greaterThanOrEqualTo(
                        builder.size(root.get("messages")), criteria.getMinMessageCount()));
            }

            if (criteria.getMaxMessageCount() != null) {
                predicates.add(
                        builder.lessThanOrEqualTo(builder.size(root.get("messages")), criteria.getMaxMessageCount()));
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

            if (criteria.getUserFirstname() != null) {
                predicates.add(builder.like(
                        builder.lower(root.join("user").get("firstName")),
                        "%" + criteria.getUserFirstname().toLowerCase() + "%"));
            }

            if (criteria.getUserLastname() != null) {
                predicates.add(builder.like(
                        builder.lower(root.join("user").get("lastName")),
                        "%" + criteria.getUserLastname().toLowerCase() + "%"));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
