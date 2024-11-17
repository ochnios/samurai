package pl.ochnios.samurai.model.entities.document;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.samurai.model.dtos.document.DocumentCriteria;

import java.util.ArrayList;
import java.util.List;

public class DocumentSpecification {

    public static Specification<DocumentEntity> create(DocumentCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getGlobalSearch() != null) {
                String globalSearchPattern = "%" + criteria.getGlobalSearch().toLowerCase() + "%";
                var titlePredicate = builder.like(builder.lower(root.get("title")), globalSearchPattern);
                var descriptionPredicate = builder.like(builder.lower(root.get("description")), globalSearchPattern);
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
                        builder.lower(root.get("title")),
                        "%" + criteria.getTitle().toLowerCase() + "%"));
            }

            if (criteria.getDescription() != null) {
                predicates.add(builder.like(
                        builder.lower(root.get("description")),
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
                        builder.lower(root.get("name")),
                        "%" + criteria.getFilename().toLowerCase() + "%"));
            }

            if (criteria.getMinSize() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("size"), criteria.getMinSize()));
            }

            if (criteria.getMaxSize() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("size"), criteria.getMaxSize()));
            }

            if (criteria.getMinUpdatedAt() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("updatedAt"), criteria.getMinUpdatedAt()));
            }

            if (criteria.getMaxUpdatedAt() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("updatedAt"), criteria.getMaxUpdatedAt()));
            }

            if (criteria.getStatus() != null) {
                predicates.add(builder.equal(root.get("status"), criteria.getStatus()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
