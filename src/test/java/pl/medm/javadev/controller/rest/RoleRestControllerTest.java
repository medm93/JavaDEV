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
import pl.medm.javadev.model.entity.Role;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
class RoleRestControllerTest {

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
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "role");
    }

    //FIND ALL ROLES
    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-role-test.sql")
    void testWhenFindAllRolesAsAdminThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/roles"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-role-test.sql")
    void testWhenFindAllRolesAsUserThenReturnStatusIsForbidden() throws Exception {
        mvc.perform(get("/roles"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-role-test.sql")
    void testWhenFindAllRolesAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        mvc.perform(get("/roles"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //CREATE ROLE
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenCreateRoleAsAdminThenReturnStatusIsCreated() throws Exception {
        Role role = new Role("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/roles").content(mapper.writeValueAsString(role)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenCreateRoleAsAdminThenReturnStatusIsBadRequest() throws Exception {
        Role role = new Role();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/roles").content(mapper.writeValueAsString(role)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-role-test.sql")
    void testWhenCreateRoleAsAdminThenReturnStatusIsConflict() throws Exception {
        Role role = new Role("ROLE_USER");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/roles").content(mapper.writeValueAsString(role)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenCreateRoleAsUserThenReturnStatusIsForbidden() throws Exception {
        Role role = new Role("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/roles").content(mapper.writeValueAsString(role)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testWhenCreateRoleAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        Role role = new Role("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/roles").content(mapper.writeValueAsString(role)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //FIND ROLE BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-role-test.sql")
    void testWhenFindRoleByIdAsAdminThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/roles/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenFindRoleByIdAsAdminThenReturnStatusIsNotFound() throws Exception {
        mvc.perform(get("/roles/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-role-test.sql")
    void testWhenFindRoleByIdAsUserThenReturnStatusIsForbidden() throws Exception {
        mvc.perform(get("/roles/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-role-test.sql")
    void testWhenFindRoleByIdAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        mvc.perform(get("/roles/{id}", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //UPDATE ROLE BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-role-test.sql")
    void testWhenUpdateRoleByIdAsAdminThenReturnStatusIsNoContent() throws Exception {
        Role role = new Role("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/roles/{id}", 2L).content(mapper.writeValueAsString(role)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateRoleByIdAsAdminThenReturnStatusIsNotFound() throws Exception {
        Role role = new Role("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/roles/{id}", 2L).content(mapper.writeValueAsString(role)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-role-test.sql")
    void testWhenUpdateRoleByIdAsAdminThenReturnStatusIsBadRequest() throws Exception {
        Role role = new Role();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/roles/{id}", 2L).content(mapper.writeValueAsString(role)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-role-test.sql")
    void testWhenUpdateRoleByIdAsUserThenReturnStatusIsForbidden() throws Exception {
        Role role = new Role("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/roles/{id}", 2L).content(mapper.writeValueAsString(role)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-role-test.sql")
    void testWhenUpdateRoleByIdAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        Role role = new Role("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/roles/{id}", 2L).content(mapper.writeValueAsString(role)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    //DELETE ROLE BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-role-test.sql")
    void testWhenDeleteRoleByIdAsAdminThenReturnStatusIsNoContent() throws Exception {
        mvc.perform(delete("/roles/{id}", 2L))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenDeleteRoleByIdAsAdminThenReturnStatusIsNotFound() throws Exception {
        mvc.perform(delete("/roles/{id}", 2L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-role-test.sql")
    void testWhenDeleteRoleByIdAsAdminThenReturnStatusIsForbidden() throws Exception {
        mvc.perform(delete("/roles/{id}", 1L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "USER")
    @Sql("/data-role-test.sql")
    void testWhenDeleteRoleByIdAsUserThenReturnStatusIsForbidden() throws Exception {
        mvc.perform(delete("/roles/{id}", 2L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    @Sql("/data-role-test.sql")
    void testWhenDeleteRoleByIdAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        mvc.perform(delete("/roles/{id}", 2L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
