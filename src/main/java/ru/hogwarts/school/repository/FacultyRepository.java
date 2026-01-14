package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Collection<Faculty> findByColorIgnoreCase(String color);

    Collection<Faculty> findByNameIgnoreCase(String name);

    @Query(value = "SELECT name FROM faculty ORDER BY LENGTH(name) DESC LIMIT 1", nativeQuery = true)
    String findLongestName();
}