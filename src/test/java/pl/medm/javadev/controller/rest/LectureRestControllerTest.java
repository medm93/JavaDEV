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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "user_lecture", "user_role", "lecture", "user", "role");
    }

    //FIND ALL LECTURES
    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-lecture-test.sql")
    void testWhenFindAllLecturesAsAdminThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/lectures"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-lecture-test.sql")
    void testWhenFindAllLecturesAsUserThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/lectures"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-lecture-test.sql")
    void testWhenFindAllLecturesAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        mvc.perform(get("/lectures"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //CREATE ROLE
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenCreateLecturesAsAdminThenReturnStatusIsCreated() throws Exception {
        Lecture lecture = new Lecture("Spring", "The basic of framework", "Tony Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenCreateLecturesAsAdminThenReturnStatusIsBadRequest() throws Exception {
        Lecture lecture = new Lecture();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-lecture-test.sql")
    void testWhenCreateLecturesAsAdminThenReturnStatusIsConflict() throws Exception {
        Lecture lecture = new Lecture("Java 8", "The basic of language", "Tony Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenCreateLecturesAsUserThenReturnStatusIsForbidden() throws Exception {
        Lecture lecture = new Lecture("Spring", "The basic of framework", "Tony Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testWhenCreateLecturesAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        Lecture lecture = new Lecture("Spring", "The basic of framework", "Tony Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //FIND LECTURE BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-lecture-test.sql")
    void testWhenFindLectureByIdAsAdminThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenFindLectureByIdAsAdminThenReturnStatusIsNotFound() throws Exception {
        mvc.perform(get("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-lecture-test.sql")
    void testWhenFindLectureByIdAsUserThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenFindLectureByIdAsUserThenReturnStatusIsNotFound() throws Exception {
        mvc.perform(get("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-lecture-test.sql")
    void testWhenFindLectureByIdAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        mvc.perform(get("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //UPDATE LECTURE BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-lecture-test.sql")
    void testWhenUpdateLectureByIdAsAdminThenReturnStatusIsNoContent() throws Exception {
        Lecture lecture = new Lecture("Hibernate", "The basic of framework", "Tony Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-lecture-test.sql")
    void testWhenUpdateLectureByIdAsAdminThenReturnStatusIsBadRequest() throws Exception {
        Lecture lecture = new Lecture();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateLectureByIdAsAdminThenReturnStatusIsNotFound() throws Exception {
        Lecture lecture = new Lecture("Hibernate", "The basic of framework", "Tony Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-lecture-test.sql")
    void testWhenUpdateLectureByIdAsAdminThenReturnStatusIsConflict() throws Exception {
        Lecture lecture = new Lecture("Java 8", "The basic of language", "Tony Stark", true);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-lecture-test.sql")
    void testWhenUpdateLectureByIdAsUserThenReturnStatusIsForbidden() throws Exception {
        Lecture lecture = new Lecture("Java 8", "The basic of language", "Tony Stark", true);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-lecture-test.sql")
    void testWhenUpdateLectureByIdAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        Lecture lecture = new Lecture("Java 8", "The basic of language", "Tony Stark", true);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //DELETE LECTURE BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-lecture-test.sql")
    void testWhenDeleteLectureByIdAsAdminThenReturnStatusInNoContent() throws Exception {
        mvc.perform(delete("/lectures/{id}", 2L))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenDeleteLectureByIdAsAdminThenReturnStatusInNotFound() throws Exception {
        mvc.perform(delete("/lectures/{id}", 2L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-lecture-test.sql")
    void testWhenDeleteLectureByIdAsAdminThenReturnStatusInForbidden() throws Exception {
        mvc.perform(delete("/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-lecture-test.sql")
    void testWhenDeleteLectureByIdAsUserThenReturnStatusInForbidden() throws Exception {
        mvc.perform(delete("/lectures/{id}", 2L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-lecture-test.sql")
    void testWhenDeleteLectureByIdAsAnonymousUserThenReturnStatusInUnauthorized() throws Exception {
        mvc.perform(delete("/lectures/{id}", 2L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //FIND ALL LECTURE USERS
    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-lecture-test.sql")
    void testWhenFindAllLectureUsersByIdAsAdminThenReturnStatusInOk() throws Exception {
        mvc.perform(get("/lectures/{id}/users", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-lecture-test.sql")
    void testWhenFindAllLectureUsersByIdAsUserThenReturnStatusInForbidden() throws Exception {
        mvc.perform(get("/lectures/{id}/users", 1L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-lecture-test.sql")
    void testWhenFindAllLectureUsersByIdAsAnonymousUserThenReturnStatusInUnauthorized() throws Exception {
        mvc.perform(get("/lectures/{id}/users", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}