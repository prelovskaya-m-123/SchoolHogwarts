package ru.hogwarts.school.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

@Service
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Override
    public Faculty addFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    @Override
    public Faculty editFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    @Override
    public ResponseEntity<Faculty> findFaculty(long id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Faculty> facultyOptional = facultyRepository.findById(id);

        if (facultyOptional.isPresent()) {
            return ResponseEntity.ok(facultyOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> deleteFaculty(long id) {
// Проверка на недопустимые значения id
        if (id <= 0) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }

        Optional<Faculty> facultyOptional = facultyRepository.findById(id);
        if (!facultyOptional.isPresent()) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        facultyRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @Override
    public Collection<Faculty> findByColorIgnoreCase(String color) {
        return facultyRepository.findByColorIgnoreCase(color);
    }

    @Override
    public Collection<Faculty> findByNameIgnoreCase(String name) {
        return facultyRepository.findByNameIgnoreCase(name);
    }

    @Override
    public Collection<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }
}
