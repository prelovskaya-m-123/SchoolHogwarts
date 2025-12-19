package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentRepository studentRepository;

    private static final String BASE_URL = "/faculty";


    @Test
    void testCreateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        Faculty savedFaculty = new Faculty();
        savedFaculty.setId(1L);
        savedFaculty.setName("Gryffindor");
        savedFaculty.setColor("Red");
        when(facultyRepository.save(any())).thenReturn(savedFaculty);

        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                BASE_URL, faculty, Faculty.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Gryffindor", response.getBody().getName());
        assertEquals("Red", response.getBody().getColor());
    }

    @Test
    void testGetFacultyById() {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Slytherin");
        faculty.setColor("Green");

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        ResponseEntity<Faculty> response = restTemplate.getForEntity
                (BASE_URL + "/1", Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Slytherin", response.getBody().getName());
        assertEquals("Green", response.getBody().getColor());
    }



    @Test
    void testUpdateFaculty() {

        Faculty faculty = new Faculty();
        faculty.setName("Ravenclaw");
        faculty.setColor("Blue");
        faculty.setId(1L);

        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setId(1L);
        updatedFaculty.setName("Ravenclaw");
        updatedFaculty.setColor("Bronze");


        when(facultyRepository.save(any(Faculty.class))).thenAnswer(invocation -> {
                    Faculty arg = invocation.getArgument(0);
                    return arg;
                });

        ResponseEntity<Faculty> postResponse = restTemplate.postForEntity(
                BASE_URL,
                faculty,
                Faculty.class
        );

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());
        assertEquals("Blue", postResponse.getBody().getColor());


        ResponseEntity<Faculty> putResponse = restTemplate.exchange(
                BASE_URL,
                HttpMethod.PUT,
                new HttpEntity<>(updatedFaculty),
                Faculty.class
        );

        assertEquals(HttpStatus.OK, putResponse.getStatusCode());
        assertEquals("Bronze", putResponse.getBody().getColor(),"Цвет факультета должен измениться на Bronze после обновления");
    }


    @Test
    void testDeleteFaculty() {

        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        when(facultyRepository.findById(eq(1L)))
                .thenReturn(Optional.of(faculty))
                .thenReturn(Optional.empty());
        doNothing().when(facultyRepository).deleteById(eq(1L));

        restTemplate.delete(BASE_URL + "/1");

        verify(facultyRepository, times(1)).findById(eq(1L));
        verify(facultyRepository, times(1)).deleteById(eq(1L));


        ResponseEntity<Faculty> response = restTemplate.getForEntity
                (BASE_URL + "/1", Faculty.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "Факультет должен быть удалён и недоступен по GET");
    }


    @Test
    void testGetFacultyStudents() {

        Long facultyId = 1L;

        Faculty faculty = new Faculty();
        faculty.setId(facultyId);
        faculty.setName("Hufflepuff");
        faculty.setColor("Yellow");

        List<Student> students = new ArrayList<>();

        Student student1 = new Student();
        student1.setId(101L);
        student1.setName("Hannah A");
        student1.setAge(14);
        student1.setFaculty(faculty);
        students.add(student1);


        Student student2 = new Student();
        student2.setId(102L);
        student2.setName("Mike B");
        student2.setAge(13);
        student2.setFaculty(faculty);
        students.add(student2);

        faculty.setStudents(students);


        when(facultyRepository.findById(facultyId))
                .thenReturn(Optional.of(faculty));


        ResponseEntity<Collection<Student>> response = restTemplate.exchange(
                BASE_URL + "/" + facultyId + "/students",
                org.springframework.http.HttpMethod.GET,
                null,
                new org.springframework.core.ParameterizedTypeReference<Collection<Student>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Статус ответа должен быть 200 OK");

        Collection<Student> body = response.getBody();
        assertNotNull(body, "Тело ответа не должно быть null");
        assertEquals(2, body.size(), "Должно быть возвращено 2 студента");


        assertTrue(students.stream().anyMatch(s ->
                        s.getId() == 101L &&
                                s.getName().equals("Hannah A") &&
                                s.getAge() == 14),
                "Студент Hannah A должен быть в ответе");


        assertTrue(students.stream().anyMatch(s ->
                        s.getId() == 102L &&
                                s.getName().equals("Mike B") &&
                                s.getAge() == 13),
                "Студент Mike B должен быть в ответе");
    }


    @Test
    void testGetAllFaculties() {
        Faculty faculty1 = new Faculty();
        faculty1.setId(1L);
        faculty1.setName("Gryffindor");
        faculty1.setColor("Red");

        Faculty faculty2 = new Faculty();
        faculty2.setId(2L);
        faculty2.setName("Hufflepuff");
        faculty2.setColor("Yellow");

        when(facultyRepository.findAll()).thenReturn(List.of(faculty1, faculty2));


        ResponseEntity<Collection<Faculty>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Faculty>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

    }
}

