package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable Long id) {
        return studentService.findStudent(id);
    }


    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student createdStudent = studentService.addStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student foundStudent = studentService.editStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping ("/by-age")
    public ResponseEntity<Collection<Student>> findStudents(@RequestParam(required = false) Integer age) {
        if (age > 0) {
            return ResponseEntity.ok(studentService.findByAge(age));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/age-range")
    public ResponseEntity<Collection<Student>> findStudentsByAgeRange(
            @RequestParam int minAge,
            @RequestParam int maxAge
    ) {
        if (minAge < 0 || maxAge < 0 || minAge > maxAge) {
            return ResponseEntity.badRequest().build();
        }
        Collection<Student> students = studentService.findByAgeBetween(minAge, maxAge);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> getStudentFaculty(@PathVariable Long id) {
        ResponseEntity<Student> studentResponse = studentService.findStudent(id);
        if (studentResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            return ResponseEntity.notFound().build();
        }

        Student student = studentResponse.getBody();
        Faculty faculty = student.getFaculty();

        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(faculty);
    }

    @GetMapping("/names/starting-with-a")
    public ResponseEntity<List<String>> getNamesStartingWithA() {
        List<String> names = studentService.getAllNamesStartingWithA();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/average-age")
    public ResponseEntity<Double> getAverageAge() {
        double averageAge = studentService.getAverageAge();
        return ResponseEntity.ok(averageAge);
    }

    @GetMapping("/print-parallel")
    public ResponseEntity<Void> printStudentsParallel() {
        Collection<Student> studentsCollection = studentService.getAllStudents();

       List<Student> students = new ArrayList<>(studentsCollection);

        if (students.size() < 6) {
            students.stream()
                    .map(Student::getName)
                    .filter(name -> name != null && !name.isEmpty())
                    .forEach(System.out::println);
            return ResponseEntity.ok().build();
        }

        System.out.println(students.get(0).getName());
        System.out.println(students.get(1).getName());

        Thread thread1 = new Thread(() -> {
            System.out.println(students.get(2).getName());
            System.out.println(students.get(3).getName());
        });

        Thread thread2 = new Thread(() -> {
            System.out.println(students.get(4).getName());
            System.out.println(students.get(5).getName());
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    private synchronized void printStudentName(String name) {
        if (name != null && !name.isEmpty()) {
            System.out.println(name);
        }
    }

    @GetMapping("/print-synchronized")
    public ResponseEntity<Void> printStudentsSynchronized() {
        Collection<Student> studentsCollection = studentService.getAllStudents();
        List<Student> students = new ArrayList<>(studentsCollection);

        if (students.size() < 6) {
            students.stream()
                    .map(Student::getName)
                    .forEach(this::printStudentName);
            return ResponseEntity.ok().build();
        }

        printStudentName(students.get(0).getName());
        printStudentName(students.get(1).getName());

        Thread thread1 = new Thread(() -> {
            printStudentName(students.get(2).getName());
            printStudentName(students.get(3).getName());
        });

        Thread thread2 = new Thread(() -> {
            printStudentName(students.get(4).getName());
            printStudentName(students.get(5).getName());
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }
}




