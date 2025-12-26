package ru.hogwarts.school.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(StudentController.class)
@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetStudentInfo() throws Exception {
        Long studentId = 1L;
        Student student = new Student(studentId, "Test Student", 18);
        when(studentService.findStudent(studentId)).thenReturn(ResponseEntity.ok(student));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(studentId.longValue()))
                .andExpect(jsonPath("$.name").value("Test Student"))
                .andExpect(jsonPath("$.age").value(18));
    }

    @Test
    void testCreateStudent() throws Exception {
        Student student = new Student(0L, "New Student", 17);
        Student createdStudent = new Student(1L, "New Student", 17);
        when(studentService.addStudent(student)).thenReturn(createdStudent);

        String studentJson = objectMapper.writeValueAsString(student);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Student"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void testEditStudent() throws Exception {
        Student student = new Student(1L, "Updated Student", 18);
        when(studentService.editStudent(student)).thenReturn(student);

        String studentJson = objectMapper.writeValueAsString(student);

        mockMvc.perform(MockMvcRequestBuilders.put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Student"))
                .andExpect(jsonPath("$.age").value(18));
    }

    @Test
    void testDeleteStudent() throws Exception {
        Long studentId = 1L;

        when(studentService.deleteStudent(studentId))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(MockMvcRequestBuilders.delete("/student/{id}", studentId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllStudents() throws Exception {
        Collection<Student> students = List.of(
                new Student(1L, "Student 1", 17),
                new Student(2L, "Student 2", 18)
        );
        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Student 1"))
                .andExpect(jsonPath("$[1].name").value("Student 2"));
    }

    @Test
    void testFindStudentsByAge() throws Exception {
        Integer age = 17;
        Collection<Student> students = List.of(new Student(1L, "Age 17", 17));
        when(studentService.findByAge(age)).thenReturn(students);


        mockMvc.perform(MockMvcRequestBuilders.get("/student/by-age?age=17"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Age 17"))
                .andExpect(jsonPath("$[0].age").value(17));
    }

    @Test
    void testFindStudentsByAgeRange() throws Exception {
        Collection<Student> students = List.of(
                new Student(1L, "Age 17", 17),
                new Student(2L, "Age 18", 18)
        );
        when(studentService.findByAgeBetween(17, 18)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/age-range?minAge=17&maxAge=18"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Age 17"))
                .andExpect(jsonPath("$[1].name").value("Age 18"));
    }

    @Test
    void testGetStudentFaculty() throws Exception {
        Long studentId = 1L;
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        Student student = new Student(studentId, "Student with Faculty", 17);
        student.setFaculty(faculty);
        when(studentService.findStudent(studentId)).thenReturn(ResponseEntity.ok(student));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}/faculty", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }
}