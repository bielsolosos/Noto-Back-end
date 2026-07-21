package br.dev.bielsolosos.noto.domain.media.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import br.dev.bielsolosos.noto.domain.media.model.MediaR2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class MediaSpecification implements Specification<MediaR2> {

    private final UUID userId;
    private final String filter;

    public MediaSpecification(UUID userId, String filter) {
        this.userId = userId;
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<MediaR2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("user").get("id"), userId));

        if (filter != null && !filter.isBlank()) {
            String pattern = "%" + filter.toLowerCase().trim() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("filename")), pattern),
                    cb.like(cb.lower(root.get("url")), pattern)
            ));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
