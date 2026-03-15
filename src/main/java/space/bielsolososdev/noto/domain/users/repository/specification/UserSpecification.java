package space.bielsolososdev.noto.domain.users.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import space.bielsolososdev.noto.domain.users.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UserSpecification implements Specification<User> {

    private final String filter;
    private final Boolean isActive;
    private final LocalDateTime createdAfter;
    private final LocalDateTime createdBefore;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter != null && !filter.isBlank()) {
            String pattern = "%" + filter.toLowerCase().trim() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("username")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)
            ));
        }

        if (isActive != null) {
            predicates.add(cb.equal(root.get("active"), isActive));
        }

        if (createdAfter != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
        }

        if (createdBefore != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdBefore));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}

