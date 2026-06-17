package br.dev.bielsolosos.noto.domain.pages.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import br.dev.bielsolosos.noto.domain.pages.enums.PageSortByEnum;
import br.dev.bielsolosos.noto.domain.pages.enums.PageSortOrderEnum;
import br.dev.bielsolosos.noto.domain.pages.model.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class PageSpecification implements Specification<Page> {

    private final UUID userId;
    private final String query;
    private final PageSortByEnum sortBy;
    private final PageSortOrderEnum sortOrder;

    @Override
    public Predicate toPredicate(Root<Page> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("user").get("id"), userId));

        if (query != null && !query.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("title")), "%" + query.toLowerCase().trim() + "%"));
        }

        String sortField = getSortField();
        if (sortOrder == PageSortOrderEnum.ASC) {
            cq.orderBy(cb.asc(root.get(sortField)));
        } else {
            cq.orderBy(cb.desc(root.get(sortField)));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private String getSortField() {
        if (sortBy == PageSortByEnum.CREATED_AT) {
            return "createdAt";
        } else if (sortBy == PageSortByEnum.TITLE) {
            return "title";
        }
        return "updatedAt";
    }
}
