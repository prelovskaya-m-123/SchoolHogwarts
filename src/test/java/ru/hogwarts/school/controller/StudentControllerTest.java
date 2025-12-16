package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "/student";

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    void testGetStudentFaculty() {

        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("Red");
        Faculty savedFaculty = facultyRepository.save(faculty);

        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(11);
        student.setFaculty(savedFaculty);
        Student savedStudent = studentRepository.save(student);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                BASE_URL + "/" + savedStudent.getId() + "/faculty",
                Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        assertEquals("Gryffindor", response.getBody().getName(), "Название факультета не совпадает");
        assertEquals("Red", response.getBody().getColor(), "Цвет факультета не совпадает");
    }

    @Test
    void testGetStudentInfo() {
        Student student = new Student();
        student.setName("Hermione Granger");
        student.setAge(12);
        Student savedStudent = studentRepository.save(student);

        ResponseEntity<Student> response = restTemplate.getForEntity
                (BASE_URL + "/" + savedStudent.getId(), Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        assertEquals("Hermione Granger", response.getBody().getName(), "Имя студента не совпадает");
        assertEquals(12, response.getBody().getAge(), "Возраст студента не совпадает");
    }

    @Test
    void testCreateStudent() {
        Student student = createTestStudent();

        ResponseEntity<Student> response = restTemplate.postForEntity
                (BASE_URL, student, Student.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Статус ответа должен быть CREATED");
        assertNotNull(response.getBody().getId(), "ID студента должен быть назначен");
        assertEquals("Harry Potter", response.getBody().getName(), "Имя созданного студента не совпадает");
    }

    @Test
    void testEditStudent() {

        Student student = createTestStudent();
        ResponseEntity<Student> postResponse = restTemplate.postForEntity
                (BASE_URL, student, Student.class);
        Long studentId = postResponse.getBody().getId();

        Student updatedStudent = new Student();
        updatedStudent.setId(studentId);
        updatedStudent.setName("Hermione Granger");
        updatedStudent.setAge(12);


        ResponseEntity<Student> putResponse = restTemplate.exchange
                (BASE_URL, HttpMethod.PUT, new HttpEntity<>(updatedStudent), Student.class);


        assertEquals(HttpStatus.OK, putResponse.getStatusCode(), "Статус ответа должен быть OK");
        assertEquals("Hermione Granger", putResponse.getBody().getName(), "Имя обновлённого студента не совпадает");
        assertEquals(12, putResponse.getBody().getAge(), "Возраст обновлённого студента не совпадает");
    }

    @Test
    void testDeleteStudent() {
        Student student = createTestStudent();
        ResponseEntity<Student> postResponse = restTemplate.postForEntity
                (BASE_URL, student, Student.class);
        Long studentId = postResponse.getBody().getId();

        restTemplate.delete(BASE_URL + "/" + studentId);

        ResponseEntity<Student> getResponse = restTemplate.getForEntity
                (BASE_URL + "/" + studentId, Student.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode(), "Статус ответа должен быть NOT_FOUND");
    }

    @Test
    void testGetAllStudents() {
        restTemplate.postForEntity(BASE_URL, createTestStudent(), Student.class);
        restTemplate.postForEntity(BASE_URL, createTestStudent(), Student.class);

        ResponseEntity<Collection<Student>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        assertTrue(response.getBody().size() >= 2, "Должно быть не менее 2 студентов");
    }

    @Test
    void testFindStudentsByAge() {
        Student student = createTestStudent();
        restTemplate.postForEntity(BASE_URL, student, Student.class);

        Map<String, Integer> params = new HashMap<>();
        params.put("age", 11);

        ResponseEntity<Collection<Student>> response = restTemplate.exchange(
                BASE_URL + "/by-age?age={age}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {},
                params
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        assertFalse(response.getBody().isEmpty(), "Результат поиска не должен быть пустым");
    }

    @Test
    void testFindStudentsByAgeRange() {
        Student student = createTestStudent();
        restTemplate.postForEntity(BASE_URL, student, Student.class);

        Map<String, Object> params = new HashMap<>();
        params.put("minAge", 10);
        params.put("maxAge", 12);

        ResponseEntity<Collection<Student>> response = restTemplate.exchange(
                BASE_URL + "/age-range?minAge={minAge}&maxAge={maxAge}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {},
                params
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        assertFalse(response.getBody().isEmpty(), "Результат поиска не должен быть пустым");
    }


    private Student createTestStudent() {
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(11);
        return student;
    }

    private Faculty createTestFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("Red");
        return faculty;
    }
}