package ru.hogwarts.school.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

@Service
public class FacultyServiceImpl implements FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyServiceImpl.class);

    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Override
    public Faculty addFaculty(Faculty faculty) {
        logger.info("Was invoked method to add new faculty: name={}, color={}", faculty.getName(), faculty.getColor());
        Faculty saved = facultyRepository.save(faculty);
        logger.debug("Faculty saved with id={}", saved.getId());
        return saved;
    }

    @Override
    public Faculty editFaculty(Faculty faculty) {
        logger.info("Was invoked method to edit faculty with id={}, new name={}, color={}",
                faculty.getId(), faculty.getName(), faculty.getColor());
        Faculty updated = facultyRepository.save(faculty);
        logger.debug("Faculty updated successfully: id={}", updated.getId());
        return updated;
    }

    @Override
    public ResponseEntity<Faculty> findFaculty(long id) {
        logger.info("Was invoked method to find faculty by id={}", id);
        if (id <= 0) {
            logger.error("Invalid faculty id: {}", id);
            return ResponseEntity.badRequest().build();
        }

        Optional<Faculty> facultyOptional = facultyRepository.findById(id);

        if (facultyOptional.isPresent()) {
            logger.debug("Faculty found: id={}, name={}", id, facultyOptional.get().getName());
            return ResponseEntity.ok(facultyOptional.get());
        } else {
            logger.warn("Faculty not found with id={}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> deleteFaculty(long id) {
        logger.info("Was invoked method to delete faculty with id={}", id);
        if (id <= 0) {
            logger.error("Invalid faculty id for deletion: {}", id);
            return ResponseEntity.badRequest().build();
        }

        Optional<Faculty> facultyOptional = facultyRepository.findById(id);
        if (!facultyOptional.isPresent()) {
            logger.warn("Faculty to delete not found with id={}", id);
            return ResponseEntity.notFound().build();
        }

        facultyRepository.deleteById(id);
        logger.info("Faculty with id={} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public Collection<Faculty> findByColorIgnoreCase(String color) {
        logger.info("Was invoked method findByColorIgnoreCase with color='{}'", color);

        if (color == null || color.isBlank()) {
            logger.warn("Color parameter is null or blank. Returning empty collection.");
            return Collections.emptyList();
        }

        Collection<Faculty> faculties = facultyRepository.findByColorIgnoreCase(color);

        if (faculties.isEmpty()) {
            logger.warn("No faculties found with color='{}'", color);
        } else {
            logger.debug("Found {} faculties with color='{}'", faculties.size(), color);
        }

        return faculties;
    }

    @Override
    public Collection<Faculty> findByNameIgnoreCase(String name) {
        logger.info("Was invoked method findByNameIgnoreCase with name='{}'", name);

        if (name == null || name.isBlank()) {
            logger.warn("Name parameter is null or blank. Returning empty collection.");
            return Collections.emptyList();
        }

        Collection<Faculty> faculties = facultyRepository.findByNameIgnoreCase(name);

        if (faculties.isEmpty()) {
            logger.warn("No faculties found with name='{}'", name);
        } else {
            logger.debug("Found {} faculties with name='{}'", faculties.size(), name);
        }

        return faculties;
    }

    @Override
    public Collection<Faculty> getAllFaculties() {
        logger.info("Was invoked method getAllFaculties");

        Collection<Faculty> allFaculties = facultyRepository.findAll();


        if (allFaculties.isEmpty()) {
            logger.warn("No faculties found in the database.");
        } else {
            logger.debug("Retrieved {} faculties from database", allFaculties.size());
        }
        return allFaculties;
    }
}