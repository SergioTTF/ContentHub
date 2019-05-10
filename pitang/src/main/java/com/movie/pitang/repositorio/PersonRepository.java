package com.movie.pitang.repositorio;

import com.movie.pitang.models.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long> {
    Page<Person> findByNameLike(String name, Pageable pageable);
    Person findByName(String name);
}