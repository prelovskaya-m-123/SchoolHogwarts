package ru.hogwarts.school.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    private final String BASE_URL = "/student";
//
//    private Student createTestStudent() {
//        Student student = new Student();
//        student.setName("Harry Potter");
//        student.setAge(11);
//        return student;
//    }
//
//    private Faculty createTestFaculty() {
//        Faculty faculty = new Faculty();
//        faculty.setName("Gryffindor");
//        faculty.setColor("Red");
//        return faculty;
//    }
//
//
//    // Тест: GET /student/{id} — получение студента по ID
//
//    @Test
//    void testGetStudentInfo() {
//        Student student = createTestStudent();
//        ResponseEntity<Student> postResponse = restTemplate.postForEntity(
//                BASE_URL, student, Student.class);
//        Long studentId = postResponse.getBody().getId();
//
//        ResponseEntity<Student> getResponse = restTemplate.getForEntity(
//                BASE_URL + "/" + studentId, Student.class);
//
//        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
//        assertEquals("Harry Potter", getResponse.getBody().getName());
//        assertEquals(11, getResponse.getBody().getAge());
//    }
//
//    // Тест: POST /student — создание студента
//
//    @Test
//    void testCreateStudent() {
//        Student student = createTestStudent();
//
//        ResponseEntity<Student> response = restTemplate.postForEntity
//                (BASE_URL, student, Student.class);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody().getId());
//        assertEquals("Harry Potter", response.getBody().getName());
//    }
//
//    // Тест: PUT /student — обновление студента
//
//    @Test
//    void testEditStudent() {
//        // Создаём студента
//        Student student = createTestStudent();
//        ResponseEntity<Student> postResponse = restTemplate.postForEntity(
//                BASE_URL, student, Student.class);
//        Long studentId = postResponse.getBody().getId();
//
//        Student updatedStudent = new Student();
//        updatedStudent.setId(studentId);
//        updatedStudent.setName("Hermione Granger");
//        updatedStudent.setAge(12);
//
//        ResponseEntity<Student> putResponse = restTemplate.exchange(
//                BASE_URL,
//                HttpMethod.PUT,
//                new HttpEntity<>(updatedStudent),
//                Student.class
//        );
//
//        assertEquals(HttpStatus.OK, putResponse.getStatusCode());
//        assertEquals("Hermione Granger", putResponse.getBody().getName());
//        assertEquals(12, putResponse.getBody().getAge());
//    }
//
//    // Тест: DELETE /student/{id} — удаление студента
//
//    @Test
//    void testDeleteStudent() {
//
//        Student student = createTestStudent();
//        ResponseEntity<Student> postResponse = restTemplate.postForEntity
//                (BASE_URL, student, Student.class);
//        Long studentId = postResponse.getBody().getId();
//
//        restTemplate.delete(BASE_URL + "/" + studentId);
//
//
//        ResponseEntity<Student> getResponse = restTemplate.getForEntity
//                (BASE_URL + "/" + studentId, Student.class);
//
//        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
//    }
//
//    // Тест: GET /student — получение всех студентов
//
//    @Test
//    void testGetAllStudents() {
//
//        restTemplate.postForEntity(BASE_URL, createTestStudent(), Student.class);
//        restTemplate.postForEntity(BASE_URL, createTestStudent(), Student.class);
//
//        ResponseEntity<Collection> response = restTemplate.getForEntity
//                (BASE_URL, Collection.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(response.getBody().size() >= 2);
//    }
//
//    // Тест: GET /student/by-age — поиск по возрасту
//
//    @Test
//    void testFindStudentsByAge() {
//        Student student = createTestStudent();
//        restTemplate.postForEntity(BASE_URL, student, Student.class);
//
//        Map<String, Integer> params = new HashMap<>();
//        params.put("age", 11);
//
//        ResponseEntity<Collection> response = restTemplate.getForEntity
//                (BASE_URL + "/by-age?age={age}", Collection.class, params);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertFalse(response.getBody().isEmpty());
//    }
//
//
//    // Тест: GET /student/age-range — поиск по диапазону возрастов
//
//    @Test
//    void testFindStudentsByAgeRange() {
//        Student student = createTestStudent();
//        restTemplate.postForEntity(BASE_URL, student, Student.class);
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("minAge", 10);
//        params.put("maxAge", 12);
//
//        ResponseEntity<Collection> response = restTemplate.getForEntity
//                (BASE_URL + "/age-range?minAge={minAge}&maxAge={maxAge}",
//                        Collection.class, params);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertFalse(response.getBody().isEmpty());
//    }
//
//
//    // Тест: GET /student/{id}/faculty — получение факультета студента
//
//    @Test
//    void testGetStudentFaculty() {
//
//        Faculty faculty = createTestFaculty();
//        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity
//                ("/faculty", faculty, Faculty.class);
//        Long facultyId = facultyResponse.getBody().getId();
//
//
//        Student student = createTestStudent();
//        student.setFaculty(facultyResponse.getBody());
//        ResponseEntity<Student> studentResponse = restTemplate.postForEntity
//                (BASE_URL, student, Student.class);
//        Long studentId = studentResponse.getBody().getId();
//
//
//        ResponseEntity<Faculty> response = restTemplate.getForEntity
//                (BASE_URL + "/" + studentId + "/faculty", Faculty.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Gryffindor", response.getBody().getName());
//    }
}