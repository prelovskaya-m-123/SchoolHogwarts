package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.Optional;
import java.util.Collection;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private FacultyRepository facultyRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "/student";

    private Student testStudent;
    private Faculty testFaculty;

    @BeforeEach
    void setUp() {
        testFaculty = new Faculty();
        testFaculty.setId(1L);
        testFaculty.setName("Gryffindor");
        testFaculty.setColor("Red");

        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setName("Harry Potter");
        testStudent.setAge(11);
        testStudent.setFaculty(testFaculty);
    }

    @Test
    void testGetStudentFaculty() {

        when(studentRepository.findById(eq(1L)))
                .thenReturn(Optional.of(testStudent));


        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                BASE_URL + "/1/faculty",
                Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        assertEquals("Gryffindor", response.getBody().getName(), "Название факультета не совпадает");
        assertEquals("Red", response.getBody().getColor(), "Цвет факультета не совпадает");
    }

    @Test
    void testGetStudentInfo() {
        when(studentRepository.findById(eq(1L)))
                .thenReturn(Optional.of(testStudent));

        ResponseEntity<Student> response = restTemplate.getForEntity
                (BASE_URL + "/1", Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Harry Potter", response.getBody().getName());
        assertEquals(11, response.getBody().getAge());
    }

    @Test
    void testCreateStudent() {
        Student newStudent = new Student();
        newStudent.setName("Hermione Granger");
        newStudent.setAge(12);

        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> {
                    Student saved = invocation.getArgument(0);
                    saved.setId(2L);
                    return saved;
                });

        ResponseEntity<Student> response = restTemplate.postForEntity
                (BASE_URL, newStudent, Student.class);


        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Статус ответа должен быть CREATED");
        assertNotNull(response.getBody().getId(), "ID студента должен быть назначен");
        assertEquals("Hermione Granger", response.getBody().getName());

        verify(studentRepository, times(1)).save(any(Student.class));
    }


    @Test
    void testEditStudent() {

        Student updatedStudent = new Student();
        updatedStudent.setId(1L);
        updatedStudent.setName("Ron Weasley");
        updatedStudent.setAge(13);

        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        ResponseEntity<Student> putResponse = restTemplate.exchange
                (BASE_URL, HttpMethod.PUT, new HttpEntity<>(updatedStudent), Student.class);


        assertEquals(HttpStatus.OK, putResponse.getStatusCode());
        assertEquals("Ron Weasley", putResponse.getBody().getName());
        assertEquals(13, putResponse.getBody().getAge());
    }

    @Test
    void testDeleteStudent() {
        when(studentRepository.findById(eq(1L)))
                .thenReturn(Optional.of(testStudent))
                .thenReturn(Optional.empty());

        doNothing().when(studentRepository).deleteById(eq(1L));


        restTemplate.delete(BASE_URL + "/1");

        ResponseEntity<Student> getResponse = restTemplate.getForEntity
                (BASE_URL + "/1", Student.class);


        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode(), "Статус ответа должен быть NOT_FOUND");

        verify(studentRepository, times(1)).deleteById(eq(1L));
    }

    @Test
    void testGetAllStudents() {
        when(studentRepository.findAll())
                .thenReturn(java.util.List.of(testStudent));


        ResponseEntity<Collection<Student>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() >= 1);
    }

    @Test
    void testFindStudentsByAge() {
        when(studentRepository.findByAge(eq(11)))
                .thenReturn(java.util.List.of(testStudent));

        Map<String, Integer> params = new HashMap<>();
        params.put("age", 11);

        ResponseEntity<Collection<Student>> response = restTemplate.exchange(
                BASE_URL + "/by-age?age={age}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                },
                params
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void testFindStudentsByAgeRange() {
        when(studentRepository.findByAgeBetween(eq(10), eq(12)))
                .thenReturn(java.util.List.of(testStudent));

        Map<String, Object> params = new HashMap<>();
        params.put("minAge", 10);
        params.put("maxAge", 12);

        ResponseEntity<Collection<Student>> response = restTemplate.exchange(
                BASE_URL + "/age-range?minAge={minAge}&maxAge={maxAge}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                },
                params
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }
}