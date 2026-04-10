package space.bielsolososdev.noto.domain.media.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import space.bielsolososdev.noto.domain.media.model.MediaR2;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class MediaSpecification implements Specification<MediaR2> {

    private String filter;

    @Override
    public Predicate toPredicate(Root<MediaR2> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();


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
