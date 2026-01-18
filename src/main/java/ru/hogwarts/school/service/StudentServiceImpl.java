package ru.hogwarts.school.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public StudentServiceImpl(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    @Override
    public Student addStudent(Student student) {
        Faculty faculty = student.getFaculty();
        if (faculty != null) {
            faculty.getStudents().add(student);
            facultyRepository.save(faculty);
        }
        return studentRepository.save(student);
    }

    @Override
    public Student editStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public ResponseEntity<Student> findStudent(long id) {

        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Student> studentOptional = studentRepository.findById(id);

        if (studentOptional.isPresent()) {
            return ResponseEntity.ok(studentOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> deleteStudent(long id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Student> studentOptional = studentRepository.findById(id);
        if (!studentOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        studentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Collection<Student> findByAge(int age) {
        return studentRepository.findByAge(age);
    }


    @Override
    public Collection<Student> findByAgeBetween(int minAge, int maxAge) {
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }
    @Override
    public List<String> getAllNamesStartingWithA() {
        List<Student> allStudents = studentRepository.findAll();
        return allStudents.stream()
                .map(Student::getName)
                .filter(name -> name != null && !name.isEmpty())
                .filter(name -> name.toUpperCase().startsWith("A"))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public double getAverageAge() {
        List<Student> allStudents = studentRepository.findAll();
        if (allStudents.isEmpty()) {
            return 0.0;
        }

        return allStudents.stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }
}
