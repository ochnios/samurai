package pl.ochnios.samurai.model.entities.document;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.samurai.model.dtos.document.DocumentCriteria;

public class DocumentSpecification {

    public static Specification<DocumentEntity> create(DocumentCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getGlobalSearch() != null) {
                String globalSearchPattern = "%" + criteria.getGlobalSearch().toLowerCase() + "%";
                var titlePredicate =
                        builder.like(builder.lower(root.get("title").as(String.class)), globalSearchPattern);
                var descriptionPredicate =
                        builder.like(builder.lower(root.get("description").as(String.class)), globalSearchPattern);
                var filenamePredicate = builder.like(builder.lower(root.get("name")), globalSearchPattern);
                var fullNamePredicate = builder.like(
                        builder.lower(builder.concat(
                                builder.concat(root.join("user").get("lastname"), " "),
                                root.join("user").get("firstname"))),
                        globalSearchPattern);
                predicates.add(builder.or(titlePredicate, descriptionPredicate, filenamePredicate, fullNamePredicate));
            }

            if (criteria.getTitle() != null) {
                predicates.add(builder.like(
                        builder.lower(root.get("title").as(String.class)),
                        "%" + criteria.getTitle().toLowerCase() + "%"));
            }

            if (criteria.getDescription() != null) {
                predicates.add(builder.like(
                        builder.lower(root.get("description").as(String.class)),
                        "%" + criteria.getDescription().toLowerCase() + "%"));
            }

            if (criteria.getUserFullName() != null) {
                String fullNamePattern = "%" + criteria.getUserFullName().toLowerCase() + "%";
                Predicate fullNamePredicate = builder.like(
                        builder.lower(builder.concat(
                                builder.concat(root.join("user").get("lastname"), " "),
                                root.join("user").get("firstname"))),
                        fullNamePattern);
                predicates.add(fullNamePredicate);
            }

            if (criteria.getFilename() != null) {
                predicates.add(builder.like(
                        builder.lower(root.get("name").as(String.class)),
                        "%" + criteria.getFilename().toLowerCase() + "%"));
            }

            if (criteria.getMinSize() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("size"), criteria.getMinSize()));
            }

            if (criteria.getMaxSize() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("size"), criteria.getMaxSize()));
            }

            if (criteria.getMinCreatedAt() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"), criteria.getMinCreatedAt()));
            }

            if (criteria.getMaxCreatedAt() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("createdAt"), criteria.getMaxCreatedAt()));
            }

            if (criteria.getStatus() != null) {
                predicates.add(builder.equal(root.get("status"), criteria.getStatus()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
