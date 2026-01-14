package ru.hogwarts.school.service;

import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface StudentService {

    Student addStudent(Student student);

    ResponseEntity<Student> findStudent(long id);

    Student editStudent(Student student);

    ResponseEntity<Void> deleteStudent(long id);

    Collection<Student> findByAge(int age);

    Collection<Student> getAllStudents();

    Collection<Student> findByAgeBetween(int minAge, int maxAge);

    List<String> getAllNamesStartingWithA();

    double getAverageAge();
}
