package ru.hogwarts.school.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
@ExtendWith(MockitoExtension.class)
class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private FacultyService facultyService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetFacultyInfo_ReturnsFaculty() throws Exception {
        Long facultyId = 1L;
        Faculty faculty = new Faculty(facultyId, "Gryffindor", "Red");

        when(facultyService.findFaculty(facultyId))
                .thenReturn(ResponseEntity.ok(faculty));

        mockMvc.perform(get("/faculty/{id}", facultyId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(facultyId))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }


    @Test
    void testGetFacultyInfo_NotFound() throws Exception {
        Long facultyId = 999L;
        when(facultyService.findFaculty(facultyId))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/faculty/{id}", facultyId))
                .andExpect(status().isNotFound());
    }


    @Test
    void testGetAllFaculties() throws Exception {
        Faculty gryffindor = new Faculty();
        gryffindor.setId(1L);
        gryffindor.setName("Gryffindor");
        gryffindor.setColor("Red");

        Faculty hufflepuff = new Faculty();
        hufflepuff.setId(2L);
        hufflepuff.setName("Hufflepuff");
        hufflepuff.setColor("Yellow");


        List<Faculty> faculties = List.of(gryffindor, hufflepuff);


        when(facultyRepository.findAll()).thenReturn(faculties);
        when(facultyService.getAllFaculties()).thenReturn(faculties);


        mockMvc.perform(get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Hufflepuff"))
                .andExpect(jsonPath("$.length()").value(2));
    }


    @Test
    void testCreateFaculty_Success() throws Exception {

        Faculty faculty = new Faculty();
        faculty.setId(0L);
        faculty.setName("Slytherin");
        faculty.setColor("Green");
        faculty.setStudents(List.of());

        Faculty savedFaculty = new Faculty();
        savedFaculty.setId(1L);
        savedFaculty.setName("Slytherin");
        savedFaculty.setColor("Green");
        savedFaculty.setStudents(List.of());

        when(facultyRepository.save(faculty)).thenReturn(savedFaculty);

        when(facultyService.addFaculty(faculty)).thenReturn(savedFaculty);

        String facultyJson = objectMapper.writeValueAsString(faculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(facultyJson))

                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Slytherin"))
                .andExpect(jsonPath("$.color").value("Green"))
                .andExpect(jsonPath("$.students").isArray())
                .andExpect(jsonPath("$.students.length()").value(0));
    }


    @Test
    void testEditFaculty_Success() throws Exception {
        Faculty faculty = new Faculty(1L, "Ravenclaw", "Blue");
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(faculty)).thenReturn(faculty);
        when(facultyService.editFaculty(faculty)).thenReturn(faculty);

        String facultyJson = objectMapper.writeValueAsString(faculty);

        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(facultyJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Ravenclaw"))
                .andExpect(jsonPath("$.color").value("Blue"))
                .andExpect(jsonPath("$.students").isArray())
                .andExpect(jsonPath("$.students.length()").value(0));
    }


    @Test
    void testEditFaculty_NotFound() throws Exception {
        Faculty faculty = new Faculty(999L, "Unknown", "Black");
        when(facultyRepository.findById(999L)).thenReturn(Optional.empty());


        String facultyJson = objectMapper.writeValueAsString(faculty);

        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(facultyJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteFaculty_Success() throws Exception {

        Long facultyId = 1L;
        Faculty faculty = new Faculty(facultyId, "Gryffindor", "Red");

        when(facultyService.deleteFaculty(facultyId))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/faculty/{id}", facultyId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }


    @Test
    void testGetFacultyStudents_Success() throws Exception {
        Long facultyId = 1L;
        Faculty faculty = new Faculty(facultyId, "Gryffindor", "Red");
        Student student = new Student(1L, "Harry Potter", 17);
        faculty.setStudents(List.of(student));


        when(facultyService.findFaculty(facultyId))
                .thenReturn(ResponseEntity.ok(faculty));

        mockMvc.perform(get("/faculty/{id}/students", facultyId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Harry Potter"))
                .andExpect(jsonPath("$[0].age").value(17));
    }

    @Test
    void testDeleteFaculty_NotFound() throws Exception {
        Long facultyId = 999L;

        when(facultyService.deleteFaculty(facultyId))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(delete("/faculty/{id}", facultyId))
                .andExpect(status().isNotFound());
    }


    @Test
    void testFindFacultiesByName() throws Exception {

        String name = "Gryffindor";
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName(name);
        faculty.setColor("Red");

        List<Faculty> faculties = List.of(faculty);

        when(facultyRepository.findByNameIgnoreCase(name)).thenReturn(faculties);
        when(facultyService.findByNameIgnoreCase(name)).thenReturn(faculties);


        mockMvc.perform(get("/faculty/search").param("name", name))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].color").value("Red"))
                .andExpect(jsonPath("$.length()").value(1));
    }



    @Test
    void testFindFacultiesByColor() throws Exception {
        String color = "Yellow";
        Faculty faculty = new Faculty();
        faculty.setId(2L);
        faculty.setName("Hufflepuff");
        faculty.setColor(color);

        List<Faculty> faculties = List.of(faculty);

        when(facultyRepository.findByColorIgnoreCase(color)).thenReturn(faculties);
        when(facultyService.findByColorIgnoreCase(color)).thenReturn(faculties);


        mockMvc.perform(get("/faculty/search").param("color", color))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].name").value("Hufflepuff"))
                .andExpect(jsonPath("$[0].color").value(color))
                .andExpect(jsonPath("$.length()").value(1));
    }


    @Test
    void testFindAllFacultiesWhenNoParams() throws Exception {
        Faculty gryffindor = new Faculty();
        gryffindor.setId(1L);
        gryffindor.setName("Gryffindor");
        gryffindor.setColor("Red");

        Faculty slytherin = new Faculty();
        slytherin.setId(2L);
        slytherin.setName("Slytherin");
        slytherin.setColor("Green");

        List<Faculty> expectedFaculties = List.of(gryffindor, slytherin);

        when(facultyRepository.findAll()).thenReturn(expectedFaculties);
        when(facultyService.getAllFaculties()).thenReturn(expectedFaculties);

        mockMvc.perform(get("/faculty/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Slytherin"))
                .andExpect(jsonPath("$.length()").value(2));
    }


    @Test
    void testGetFacultyStudents_NotFound() throws Exception {
        Long facultyId = 999L;
        when(facultyService.findFaculty(facultyId))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/faculty/{id}/students", facultyId))
                .andExpect(status().isNotFound());
    }
}
