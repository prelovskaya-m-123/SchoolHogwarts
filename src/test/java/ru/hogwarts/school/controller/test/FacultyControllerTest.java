package ru.hogwarts.school.controller.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
@ExtendWith(MockitoExtension.class)
class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyService facultyService;

    @Test
    void testGetFacultyInfo_ReturnsFaculty() throws Exception {
        Long facultyId = 1L;
        Faculty faculty = new Faculty(facultyId, "Gryffindor", "Red");
        when(facultyService.findFaculty(facultyId)).thenReturn(ResponseEntity.ok(faculty));


        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}", facultyId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(facultyId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Gryffindor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value("Red"));
    }

    @Test
    void testGetFacultyInfo_NotFound() throws Exception {
        Long facultyId = 999L;
        when(facultyService.findFaculty(facultyId)).thenReturn(ResponseEntity.notFound().build());


        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}", facultyId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllFaculties() throws Exception {
        Collection<Faculty> faculties = List.of(
                new Faculty(1L, "Gryffindor", "Red"),
                new Faculty(2L, "Hufflepuff", "Yellow")
        );
        when(facultyService.getAllFaculties()).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Hufflepuff"));
    }

    @Test
    void testCreateFaculty_Success() throws Exception {
        Faculty faculty = new Faculty(0L, "Slytherin", "Green");
        Faculty createdFaculty = new Faculty(1L, "Slytherin", "Green");
        when(facultyService.addFaculty(faculty)).thenReturn(createdFaculty);

        String facultyJson = """
                {"id": 0, "name": "Slytherin", "color": "Green"}
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(facultyJson))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Slytherin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value("Green"));
    }

    @Test
    void testEditFaculty_Success() throws Exception {
        Faculty faculty = new Faculty(1L, "Ravenclaw", "Blue");
        when(facultyService.editFaculty(faculty)).thenReturn(faculty);

        String facultyJson = """
                {"id": 1, "name": "Ravenclaw", "color": "Blue"}
                """;

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(facultyJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Ravenclaw"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value("Blue"));
    }

    @Test
    void testEditFaculty_NotFound() throws Exception {
        Faculty faculty = new Faculty(999L, "Unknown", "Black");
        when(facultyService.editFaculty(faculty)).thenReturn(null);


        String facultyJson = """
                {"id": 999, "name": "Unknown", "color": "Black"}
                """;

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(facultyJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteFaculty_Success() throws Exception {
        Long facultyId = 1L;
        when(facultyService.deleteFaculty(facultyId)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/{id}", facultyId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteFaculty_NotFound() throws Exception {
        Long facultyId = 999L;
        when(facultyService.deleteFaculty(facultyId)).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/{id}", facultyId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindFacultiesByName() throws Exception {
        String name = "Gryffindor";
        Collection<Faculty> faculties = List.of(new Faculty(1L, name, "Red"));
        when(facultyService.findByNameIgnoreCase(name)).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/search")
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(name))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color").value("Red"));
    }

    @Test
    void testFindFacultiesByColor() throws Exception {
        String color = "Yellow";
        Collection<Faculty> faculties = List.of(new Faculty(2L, "Hufflepuff", color));
        when(facultyService.findByColorIgnoreCase(color)).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/search")
                        .param("color", color))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Hufflepuff"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color").value(color));
    }

    @Test
    void testFindAllFacultiesWhenNoParams() throws Exception {
        Collection<Faculty> faculties = List.of(
                new Faculty(1L, "Gryffindor", "Red"),
                new Faculty(2L, "Slytherin", "Green")
        );
        when(facultyService.getAllFaculties()).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/search"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Slytherin"));
    }

    @Test
    void testGetFacultyStudents_Success() throws Exception {
        Long facultyId = 1L;
        Faculty faculty = new Faculty(facultyId, "Gryffindor", "Red");
        Student student = new Student(1L, "Harry Potter", 17);
        faculty.setStudents(List.of(student));

        when(facultyService.findFaculty(facultyId)).thenReturn(ResponseEntity.ok(faculty));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}/students", facultyId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Harry Potter"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(17));
    }

    @Test
    void testGetFacultyStudents_NotFound() throws Exception {
        Long facultyId = 999L;
        when(facultyService.findFaculty(facultyId)).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}/students", facultyId))
                .andExpect(status().isNotFound());
    }
}
