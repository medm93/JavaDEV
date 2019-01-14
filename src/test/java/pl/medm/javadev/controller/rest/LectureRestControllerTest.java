package pl.medm.javadev.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.medm.javadev.model.entity.Lecture;
import pl.medm.javadev.model.entity.User;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
class LectureRestControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mvc;

    @BeforeEach
    void setup() {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void cleanup() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "lecture", "user", "role", "user_lecture");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-test.sql")
    void testFindAllLecturesForAdmin() throws Exception {
        mvc.perform(get("/lectures"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-test.sql")
    void testFindAllLecturesForUser() throws Exception {
        mvc.perform(get("/lectures"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-test.sql")
    void testFindAllLecturesForAnonymousUser() throws Exception {
        mvc.perform(get("/lectures"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateLectureForAdminWhenLectureNotExist() throws Exception {
        Lecture lecture = new Lecture(null, "Java 8", "The basics of language", "Tony Stark", true);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-test.sql")
    void testCreateLectureForAdminWhenLectureExist() throws Exception {
        Lecture lecture = new Lecture(null, "Java 8", "The basics of language", "Tony Stark", true);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateLectureForUserWhenLectureNotExist() throws Exception {
        Lecture lecture = new Lecture(null, "Java 8", "The basics of language", "Tony Stark", true);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-test.sql")
    void testCreateLectureForUserWhenLectureExist() throws Exception {
        Lecture lecture = new Lecture(null, "Java 8", "The basics of language", "Tony Stark", true);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testCreateLectureForAnonymousUserWhenLectureNotExist() throws Exception {
        Lecture lecture = new Lecture(null, "Java 8", "The basics of language", "Tony Stark", true);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-test.sql")
    void testCreateLectureForAnonymousUserWhenLectureExist() throws Exception {
        Lecture lecture = new Lecture(null, "Java 8", "The basics of language", "Tony Stark", true);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-test.sql")
    void testFindLectureByIdForAdminWhenLectureFound() throws Exception {
        mvc.perform(get("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFindLectureByIdForAdminWhenLectureNotFound() throws Exception {
        mvc.perform(get("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-test.sql")
    void testFindLectureByIdForUserWhenLectureFound() throws Exception {
        mvc.perform(get("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testFindLectureByIdForUserWhenLectureNotFound() throws Exception {
        mvc.perform(get("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-test.sql")
    void testFindLectureByIdForAnonymousUserWhenLectureFound() throws Exception {
        mvc.perform(get("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testFindLectureByIdForAnonymousUserWhenLectureNotFound() throws Exception {
        mvc.perform(get("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-test.sql")
    void testUpdateLectureForAdminWhenLectureFound() throws Exception {
        Lecture updated = new Lecture(null, "Spring", "The basics of framework", "Johny English", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(updated)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateLectureForAdminWhenLectureNotFound() throws Exception {
        Lecture updated = new Lecture(null, "Spring", "The basics of framework", "Johny English", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(updated)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-test.sql")
    void testUpdateLectureForUserWhenLectureFound() throws Exception {
        Lecture updated = new Lecture(null, "Spring", "The basics of framework", "Johny English", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(updated)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateLectureForUserWhenLectureNotFound() throws Exception {
        Lecture updated = new Lecture(null, "Spring", "The basics of framework", "Johny English", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(updated)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-test.sql")
    void testUpdateLectureForAnonymousUserWhenLectureFound() throws Exception {
        Lecture updated = new Lecture(null, "Spring", "The basics of framework", "Johny English", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(updated)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testUpdateLectureForAnonymousUserWhenLectureNotFound() throws Exception {
        Lecture updated = new Lecture(null, "Spring", "The basics of framework", "Johny English", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(updated)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-test.sql")
    void testDeleteLectureForAdminWhenLectureFoundAndCompleted() throws Exception {
        mvc.perform(delete("/lectures/{id}", 1))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-test.sql")
    void testDeleteLectureForAdminWhenLectureFoundAndNotCompleted() throws Exception {
        mvc.perform(delete("/lectures/{id}", 2L))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteLectureForAdminWhenLectureNotFound() throws Exception {
        mvc.perform(delete("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-test.sql")
    void testDeleteLectureForUserWhenLectureFoundAndCompleted() throws Exception {
        mvc.perform(delete("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteLectureForUserWhenLectureFoundAndNotCompleted() throws Exception {
        mvc.perform(delete("/lectures/{id}", 2L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteLectureForUserWhenLectureNotFound() throws Exception {
        mvc.perform(delete("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-test.sql")
    void testDeleteLectureForAnonymousUserWhenLectureFoundCompleted() throws Exception {
        mvc.perform(delete("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-test.sql")
    void testDeleteLectureForAnonymousUserWhenLectureFoundNotCompleted() throws Exception {
        mvc.perform(delete("/lectures/{id}", 2L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void testDeleteLectureForAnonymousUserWhenLectureNotFound() throws Exception {
        mvc.perform(delete("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddUserTOLectureForAdminWhenLectureFoundAndUserFound() throws Exception {
        createTestLecture();
        createTestUser();
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures/{id}/users", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddUserTOLectureForAdminWhenLectureFoundAndUserNotFound() throws Exception {
        createTestLecture();
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures/{id}/users", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddUserTOLectureForAdminWhenLectureNotFoundUserFound() throws Exception {
        createTestUser();
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures/{id}/users", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddUserTOLectureForAdminWhenLectureNotFoundAndUserNotFound() throws Exception {
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures/{id}/users", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-test.sql")
    void testAddUserTOLectureForUserWhenLectureFoundAndUserFound() throws Exception {
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures/{id}/users", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAddUserTOLectureForUserWhenLectureFoundAndUserNotFound() throws Exception {
        createTestLecture();
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures/{id}/users", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAddUserTOLectureForUserWhenLectureNotFoundUserFound() throws Exception {
        createTestUser();
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures/{id}/users", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAddUserTOLectureForUserWhenLectureNotFoundAndUserNotFound() throws Exception {
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures/{id}/users", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    private void createTestLecture() {
        String sql = "INSERT INTO lecture(id, title, description, lecturer, completed)" +
                "VALUES (1, 'Java 8', 'The basics of language', 'Tony Stark', true)";
        jdbcTemplate.execute(sql);
    }

    private void createTestLecture(boolean completed) {
        String sql = "INSERT INTO lecture(id, title, description, lecturer, completed)" +
                "VALUES (1, 'Java 8', 'The basics of language', 'Tony Stark', " + completed + ")";
        jdbcTemplate.execute(sql);
    }

    private void createTestUser() {
        String sql = "INSERT INTO user(id, first_Name, last_Name, email, password)" +
                "VALUES (1, 'James', 'Bond', '007@gmail.com', 'zaq1@WSX')";
        jdbcTemplate.execute(sql);
    }
}
