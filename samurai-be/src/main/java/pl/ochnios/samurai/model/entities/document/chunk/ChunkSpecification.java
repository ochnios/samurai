package pl.ochnios.samurai.model.entities.document.chunk;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.samurai.model.dtos.document.chunk.ChunkCriteria;

public class ChunkSpecification {

    public static Specification<Chunk> create(ChunkCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getGlobalSearch() != null) {
                String globalSearchPattern = "%" + criteria.getGlobalSearch().toLowerCase() + "%";
                var contentPredicate = builder.like(builder.lower(root.get("content")), globalSearchPattern);
                predicates.add(builder.or(contentPredicate));
            }

            if (criteria.getContent() != null) {
                predicates.add(builder.like(
                        builder.lower(root.get("content")),
                        "%" + criteria.getContent().toLowerCase() + "%"));
            }

            if (criteria.getMinPosition() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("position"), criteria.getMinPosition()));
            }

            if (criteria.getMaxPosition() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("position"), criteria.getMaxPosition()));
            }

            if (criteria.getMinLength() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("length"), criteria.getMinLength()));
            }

            if (criteria.getMaxLength() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("length"), criteria.getMaxLength()));
            }

            if (criteria.getMinUpdatedAt() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("updatedAt"), criteria.getMinUpdatedAt()));
            }

            if (criteria.getMaxUpdatedAt() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("updatedAt"), criteria.getMaxUpdatedAt()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
