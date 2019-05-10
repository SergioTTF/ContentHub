package com.movie.pitang.utils;

import com.movie.pitang.models.Serie;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.text.WordUtils;

public class SerieSpecification {
    public static Specification<Serie> searchSerie(String title, Integer year, String language) {
        return new Specification<Serie>() {
            @Override
            public Predicate toPredicate(Root<Serie> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.or(cb.like(root.get("title"), "%"+ WordUtils.capitalize(title) + "%"),
                        cb.like(root.get("language"), language),
                        cb.equal(root.get("yearOfRelease"), year));
            }
        };
    }
}
