package ru.hogwarts.school.service;

import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import java.util.Collection;

public interface FacultyService {

    Faculty addFaculty(Faculty faculty);

    ResponseEntity<Faculty> findFaculty(long id);

    Faculty editFaculty(Faculty faculty);

    ResponseEntity<Void> deleteFaculty(long id);

    Collection<Faculty> findByColorIgnoreCase(String color);

    Collection<Faculty> findByNameIgnoreCase(String name);

    Collection<Faculty> getAllFaculties();
}