package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "/faculty";

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    void testCreateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                (BASE_URL), faculty, Faculty.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals("Gryffindor", response.getBody().getName());
        assertEquals("Red", response.getBody().getColor());
    }

    @Test
    void testGetFacultyById() {
        Faculty faculty = new Faculty();
        faculty.setName("Slytherin");
        faculty.setColor("Green");
        Faculty savedFaculty = facultyRepository.save(faculty);

        ResponseEntity<Faculty> response = restTemplate.getForEntity
                (BASE_URL + "/" + savedFaculty.getId(), Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Slytherin", response.getBody().getName());
        assertEquals("Green", response.getBody().getColor());
    }

    @Test
    void testGetFacultyStudents() {

        Faculty faculty = new Faculty();
        faculty.setName("Hufflepuff");
        faculty.setColor("Yellow");
        Faculty savedFaculty = facultyRepository.save(faculty);

        Student student1 = new Student();
        student1.setName("Hannah A");
        student1.setAge(14);
        student1.setFaculty(savedFaculty);
        studentRepository.save(student1);

        Student student2 = new Student();
        student2.setName("Mike B");
        student2.setAge(13);
        student2.setFaculty(savedFaculty);
        studentRepository.save(student2);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                BASE_URL + "/" + savedFaculty.getId(), Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Faculty reloadedFaculty = response.getBody();
        assertNotNull(reloadedFaculty);

        Collection<Student> students = reloadedFaculty.getStudents();
        assertNotNull(students);
        assertEquals(2, students.size());

        assertTrue(students.stream()
                .anyMatch(s -> s.getName().equals("Hannah A") && s.getAge() == 14));
        assertTrue(students.stream()
                .anyMatch(s -> s.getName().equals("Mike B") && s.getAge() == 13));
    }

    @Test
    void testUpdateFaculty() {

        Faculty faculty = new Faculty();
        faculty.setName("Ravenclaw");
        faculty.setColor("Blue");

        ResponseEntity<Faculty> postResponse = restTemplate.postForEntity(
                BASE_URL,
                faculty,
                Faculty.class
        );

        Long facultyId = postResponse.getBody().getId();

        faculty.setId(facultyId);
        faculty.setColor("Bronze");

        ResponseEntity<Faculty> putResponse = restTemplate.exchange(
                BASE_URL,
                HttpMethod.PUT,
                new HttpEntity<>(faculty),
                Faculty.class
        );

        assertEquals(HttpStatus.OK, putResponse.getStatusCode());
        assertEquals("Bronze", putResponse.getBody().getColor());
    }


    @Test
    void testDeleteFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Dumbledore Army");
        faculty.setColor("Gold");
        Faculty savedFaculty = facultyRepository.save(faculty);

        restTemplate.delete(BASE_URL + "/" + savedFaculty.getId());

        ResponseEntity<Faculty> response = restTemplate.getForEntity
                (BASE_URL + "/" + savedFaculty.getId(), Faculty.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
