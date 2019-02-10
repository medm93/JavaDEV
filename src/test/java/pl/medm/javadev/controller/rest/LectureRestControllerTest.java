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
import pl.medm.javadev.model.dto.LectureDTO;
import pl.medm.javadev.model.entity.Lecture;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@Sql("/data-lecture-test.sql")
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
    void testWhenFindAllLecturesAsAdminThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/api/lectures"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenFindAllLecturesAsUserThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/api/lectures"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testWhenFindAllLecturesAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        mvc.perform(get("/api/lectures"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //CREATE ROLE
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenCreateLecturesAsAdminThenReturnStatusIsCreated() throws Exception {
        LectureDTO lecture = new LectureDTO("Hibernate", "The basic of framework", "Howard Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/api/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenCreateLecturesAsAdminThenReturnStatusIsBadRequest() throws Exception {
        LectureDTO lecture = new LectureDTO();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/api/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenCreateLecturesAsAdminThenReturnStatusIsConflict() throws Exception {
        LectureDTO lecture = new LectureDTO("Java 8", "The basic of language", "Howard Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/api/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenCreateLecturesAsUserThenReturnStatusIsForbidden() throws Exception {
        LectureDTO lecture = new LectureDTO("Spring", "The basic of framework", "Howard Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/api/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testWhenCreateLecturesAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        LectureDTO lecture = new LectureDTO("Spring", "The basic of framework", "Howard Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/api/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //FIND LECTURE BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenFindLectureByIdAsAdminThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/api/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenFindLectureByIdAsAdminThenReturnStatusIsNotFound() throws Exception {
        mvc.perform(get("/api/lectures/{id}", 3L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-lecture-test.sql")
    void testWhenFindLectureByIdAsUserThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/api/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenFindLectureByIdAsUserThenReturnStatusIsNotFound() throws Exception {
        mvc.perform(get("/api/lectures/{id}", 3L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testWhenFindLectureByIdAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        mvc.perform(get("/api/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //UPDATE LECTURE BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateLectureByIdAsAdminThenReturnStatusIsNoContent() throws Exception {
        LectureDTO lecture = new LectureDTO("Hibernate", "The basic of framework", "Howard Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/api/lectures/{id}", 1L).content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-lecture-test.sql")
    void testWhenUpdateLectureByIdAsAdminThenReturnStatusIsBadRequest() throws Exception {
        LectureDTO lecture = new LectureDTO();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/api/lectures/{id}", 1L).content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateLectureByIdAsAdminThenReturnStatusIsNotFound() throws Exception {
        LectureDTO lecture = new LectureDTO("Hibernate", "The basic of framework", "Howard Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/api/lectures/{id}", 3L).content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-lecture-test.sql")
    void testWhenUpdateLectureByIdAsAdminThenReturnStatusIsConflict() throws Exception {
        LectureDTO lecture = new LectureDTO("Java 8", "The basic of language", "Howard Stark", true);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/api/lectures/{id}", 1L).content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenUpdateLectureByIdAsUserThenReturnStatusIsForbidden() throws Exception {
        LectureDTO lecture = new LectureDTO("Hibernate", "The basic of framework", "Howard Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/api/lectures/{id}", 1L).content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testWhenUpdateLectureByIdAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        LectureDTO lecture = new LectureDTO("Hibernate", "The basic of framework", "Howard Stark", false);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/api/lectures/{id}", 1L).content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //DELETE LECTURE BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenDeleteLectureByIdAsAdminThenReturnStatusInNoContent() throws Exception {
        mvc.perform(delete("/api/lectures/{id}", 2L))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenDeleteLectureByIdAsAdminThenReturnStatusInNotFound() throws Exception {
        mvc.perform(delete("/api/lectures/{id}", 3L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-lecture-test.sql")
    void testWhenDeleteLectureByIdAsAdminThenReturnStatusInForbidden() throws Exception {
        mvc.perform(delete("/api/lectures/{id}", 1L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenDeleteLectureByIdAsUserThenReturnStatusInForbidden() throws Exception {
        mvc.perform(delete("/api/lectures/{id}", 2L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testWhenDeleteLectureByIdAsAnonymousUserThenReturnStatusInUnauthorized() throws Exception {
        mvc.perform(delete("/api/lectures/{id}", 2L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //FIND ALL LECTURE USERS
    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql({"/data-user-test.sql", "/data-lecture-test.sql", "/data-user-lecture-test.sql"})
    void testWhenFindAllLectureUsersByIdAsAdminThenReturnStatusInOk() throws Exception {
        mvc.perform(get("/api/lectures/{id}/users", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql({"/data-user-test.sql", "/data-lecture-test.sql", "/data-user-lecture-test.sql"})
    void testWhenFindAllLectureUsersByIdAsAdminThenReturnStatusInNotFound() throws Exception {
        mvc.perform(get("/api/lectures/{id}/users", 3L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/data-user-test.sql", "/data-lecture-test.sql", "/data-user-lecture-test.sql"})
    void testWhenFindAllLectureUsersByIdAsUserThenReturnStatusInForbidden() throws Exception {
        mvc.perform(get("/api/lectures/{id}/users", 1L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    @Sql({"/data-user-test.sql", "/data-lecture-test.sql", "/data-user-lecture-test.sql"})
    void testWhenFindAllLectureUsersByIdAsAnonymousUserThenReturnStatusInUnauthorized() throws Exception {
        mvc.perform(get("/api/lectures/{id}/users", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}