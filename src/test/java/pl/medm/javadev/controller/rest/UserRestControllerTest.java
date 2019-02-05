package pl.medm.javadev.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
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
import pl.medm.javadev.model.entity.User;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@Sql("/data-user-test.sql")
class UserRestControllerTest {

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
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "user_lecture", "user", "lecture");
    }

    //FIND ALL USERS
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenFindAllUsersAsAdminThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenFindAllUsersAsUserThenReturnStatusIsForbidden() throws Exception {
        mvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testWhenFindAllUsersAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        mvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //CREATE USER
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenCreateUserAsAdminThenReturnStatusInCreated() throws Exception {
        User user = new User("Bruce", "Banner", "hulk@marvel.com", "zaq1@WSX", "3", "Informatics", "000003");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/users").content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenCreateUserAsAdminThenReturnStatusInBadRequest() throws Exception {
        User user = new User();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/users").content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenCreateUserAsAdminThenReturnStatusInConflict() throws Exception {
        User user = new User("Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX", "1", "Informatics", "123456");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/users").content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenCreateUserAsUserThenReturnStatusInForbidden() throws Exception {
        User user = new User("Bruce", "Banner", "hulk@marvel.com", "zaq1@WSX", "3", "Informatics", "000003");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/users").content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testWhenCreateUserAsAnonymousUserThenReturnStatusInUnauthorized() throws Exception {
        User user = new User("Bruce", "Banner", "hulk@marvel.com", "zaq1@WSX", "3", "Informatics", "000003");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/users").content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //FIND USER BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenFindUserByIdAsAdminThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/users/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenFindUserByIdAsAdminThenReturnStatusIsNotFound() throws Exception {
        mvc.perform(get("/users/{id}", 3L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenFindUserByIdAsUserThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/users/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenFindUserByIdAsUserThenReturnStatusIsNotFound() throws Exception {
        mvc.perform(get("/users/{id}", 3L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testWhenFindUserByIdAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        mvc.perform(get("/users/{id}", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //UPDATE USER BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateUserAsAdminThenReturnStatusIsNoContent() throws Exception {
        User user = new User("Bruce", "Banner", "hulk@marvel.com", null, "3", "Informatics", "000003");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateUserAsAdminThenReturnStatusIsBadRequest() throws Exception {
        User user = new User();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateUserAsAdminThenReturnStatusIsConflict() throws Exception {
        User user = new User("Steven", "Rogers", "capitan.america@marvel.com", null, "2", "Electronics", "000002");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateUserAsAdminThenReturnStatusIsNotFound() throws Exception {
        User user = new User("Bruce", "Banner", "hulk@marvel.com", null, "3", "Informatics", "000003");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}", 3L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenUpdateUserAsUserThenReturnStatusIsNoContent() throws Exception {
        User user = new User("Bruce", "Banner", "hulk@marvel.com", null, "3", "Informatics", "000003");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenUpdateUserAsUserThenReturnStatusIsBadRequest() throws Exception {
        User user = new User();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenUpdateUserAsUserThenReturnStatusIsConflict() throws Exception {
        User user = new User("Steven", "Rogers", "capitan.america@marvel.com", null, "2", "Electronics", "000002");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenUpdateUserAsUserThenReturnStatusIsNotFound() throws Exception {
        User user = new User("Bruce", "Banner", "hulk@marvel.com", null, "3", "Informatics", "000003");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}", 3L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testWhenUpdateUserAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        User user = new User("Bruce", "Banner", "hulk@marvel.com", null, "3", "Informatics", "000003");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //UPDATE USER PASSWORD BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateUserPasswordByIdAsAdminThenReturnStatusInNoContent() throws Exception {
        User user = new User(null, null, null, "xsw2!QAZ", null, null, null);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}/password", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateUserPasswordByIdAsAdminThenReturnStatusInBadRequest() throws Exception {
        User user = new User();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}/password", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateUserPasswordByIdAsAdminThenReturnStatusInNotFound() throws Exception {
        User user = new User(null, null, null, "xsw2!QAZ", null, null, null);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}/password", 3L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenUpdateUserPasswordByIdAsUserThenReturnStatusInNoContent() throws Exception {
        User user = new User(null, null, null, "xsw2!QAZ", null, null, null);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}/password", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenUpdateUserPasswordByIdAsUserThenReturnStatusInBadRequest() throws Exception {
        User user = new User();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}/password", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenUpdateUserPasswordByIdAsUserThenReturnStatusInNotFound() throws Exception {
        User user = new User(null, null, null, "xsw2!QAZ", null, null, null);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}/password", 3L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testWhenUpdateUserPasswordByIdAsAnonymousUserThenReturnStatusInUnauthorized() throws Exception {
        User user = new User(null, null, null, "xsw2!QAZ", null, null, null);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/{id}/password", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //DELETE USER BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenDeleteUserByIdAsAdminThenReturnStatusInNoContent() throws Exception {
        mvc.perform(delete("/users/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenDeleteUserByIdAsAdminThenReturnStatusInNotFound() throws Exception {
        mvc.perform(delete("/users/{id}", 3L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenDeleteUserByIdAsUserThenReturnStatusInForbidden() throws Exception {
        mvc.perform(delete("/users/{id}", 1L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testWhenDeleteUserByIdAsAnonymousUserThenReturnStatusInUnauthorized() throws Exception {
        mvc.perform(delete("/users/{id}", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //FIND ALL USER LECTURES BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql({"/data-user-test.sql", "/data-lecture-test.sql", "/data-user-lecture-test.sql"})
    void testWhenFindAllUserLecturesByIdAsAdminThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/users/{id}/lectures", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql({"/data-user-test.sql", "/data-lecture-test.sql", "/data-user-lecture-test.sql"})
    void testWhenFindAllUserLecturesByIdAsAdminThenReturnStatusIsNotFound() throws Exception {
        mvc.perform(get("/users/{id}/lectures", 3L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/data-user-test.sql", "/data-lecture-test.sql", "/data-user-lecture-test.sql"})
    void testWhenFindAllUserLecturesByIdAsUserThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/users/{id}/lectures", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/data-user-test.sql", "/data-lecture-test.sql", "/data-user-lecture-test.sql"})
    void testWhenFindAllUserLecturesByIdAsUserThenReturnStatusIsNotFound() throws Exception {
        mvc.perform(get("/users/{id}/lectures", 3L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    @Sql({"/data-user-test.sql", "/data-lecture-test.sql", "/data-user-lecture-test.sql"})
    void testWhenFindAllUserLecturesByIdAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        mvc.perform(get("/users/{id}/lectures", 3L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}