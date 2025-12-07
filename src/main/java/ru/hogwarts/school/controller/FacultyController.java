package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import java.util.Collection;


@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFacultyInfo(@PathVariable Long id) {
        return facultyService.findFaculty(id);
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> getAllFaculties() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty) {
        Faculty createdFaculty = facultyService.addFaculty(faculty);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFaculty);
    }

    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        Faculty foundFaculty = facultyService.editFaculty(faculty);
        if (foundFaculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundFaculty);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        return facultyService.deleteFaculty(id);
    }

    @GetMapping ("/search")
    public ResponseEntity<Collection<Faculty>> findFaculties(@RequestParam(required = false) String name,
                                                             @RequestParam(required = false) String color) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(facultyService.findByNameIgnoreCase(name));
        }
        if (color != null && !color.isBlank()) {
            return ResponseEntity.ok(facultyService.findByColorIgnoreCase(color));
        }
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<Collection<Student>> getFacultyStudents(@PathVariable Long id) {
        ResponseEntity<Faculty> facultyResponse = facultyService.findFaculty(id);
        if (facultyResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            return ResponseEntity.notFound().build();
        }

        Faculty faculty = facultyResponse.getBody();
        Collection<Student> students = faculty.getStudents();

        return ResponseEntity.ok(students);
    }
}