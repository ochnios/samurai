package pl.ochnios.samurai.model.entities.user;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.samurai.model.dtos.user.UserCriteria;

public class UserSpecification {

    public static Specification<User> create(UserCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getGlobalSearch() != null) {
                String globalSearchPattern = "%" + criteria.getGlobalSearch().toLowerCase() + "%";
                var usernamePredicate = builder.like(builder.lower(root.get("username")), globalSearchPattern);
                var firstnamePredicate = builder.like(builder.lower(root.get("firstname")), globalSearchPattern);
                var lastnamePredicate = builder.like(builder.lower(root.get("lastname")), globalSearchPattern);
                var emailPredicate = builder.like(builder.lower(root.get("email")), globalSearchPattern);
                predicates.add(builder.or(usernamePredicate, firstnamePredicate, lastnamePredicate, emailPredicate));
            }

            if (criteria.getFirstname() != null) {
                predicates.add(builder.like(
                        builder.lower(root.get("firstname")),
                        "%" + criteria.getFirstname().toLowerCase() + "%"));
            }

            if (criteria.getLastname() != null) {
                predicates.add(builder.like(
                        builder.lower(root.get("lastname")),
                        "%" + criteria.getLastname().toLowerCase() + "%"));
            }

            if (criteria.getEmail() != null) {
                predicates.add(builder.like(
                        builder.lower(root.get("email")),
                        "%" + criteria.getEmail().toLowerCase() + "%"));
            }

            if (criteria.getRole() != null) {
                predicates.add(builder.equal(root.get("role"), criteria.getRole()));
            }

            if (criteria.getMinCreatedAt() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"), criteria.getMinCreatedAt()));
            }

            if (criteria.getMaxCreatedAt() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("createdAt"), criteria.getMaxCreatedAt()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
