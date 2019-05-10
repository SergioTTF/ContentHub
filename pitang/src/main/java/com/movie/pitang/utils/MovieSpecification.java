package com.movie.pitang.utils;

import com.movie.pitang.models.Movie;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.text.WordUtils;

public class MovieSpecification {

    public static Specification<Movie> searchMovie(String title, Integer year, String language) {
        return new Specification<Movie>() {
            @Override
            public Predicate toPredicate(Root<Movie> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.or(cb.like(root.get("title"),"%"+ WordUtils.capitalize(title)+ "%"),
                        cb.like(root.get("language"), language),
                        cb.equal(root.get("yearOfRelease"), year));
            }
        };
    }
}
