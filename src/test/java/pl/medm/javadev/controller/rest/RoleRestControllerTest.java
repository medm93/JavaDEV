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
import pl.medm.javadev.model.dto.RoleDTO;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@Sql("/data-role-test.sql")
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
    void testWhenFindAllRolesAsAdminThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/api/roles"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenFindAllRolesAsUserThenReturnStatusIsForbidden() throws Exception {
        mvc.perform(get("/api/roles"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testWhenFindAllRolesAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        mvc.perform(get("/api/roles"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //CREATE ROLE
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenCreateRoleAsAdminThenReturnStatusIsCreated() throws Exception {
        RoleDTO dto = new RoleDTO("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/api/roles").content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenCreateRoleAsAdminThenReturnStatusIsBadRequest() throws Exception {
        RoleDTO dto = new RoleDTO();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/api/roles").content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenCreateRoleAsAdminThenReturnStatusIsConflict() throws Exception {
        RoleDTO dto = new RoleDTO("ROLE_USER");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/api/roles").content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenCreateRoleAsUserThenReturnStatusIsForbidden() throws Exception {
        RoleDTO dto = new RoleDTO("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/api/roles").content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testWhenCreateRoleAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        RoleDTO dto = new RoleDTO("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/api/roles").content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //FIND ROLE BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenFindRoleByIdAsAdminThenReturnStatusIsOk() throws Exception {
        mvc.perform(get("/api/roles/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenFindRoleByIdAsAdminThenReturnStatusIsNotFound() throws Exception {
        mvc.perform(get("/api/roles/{id}", 3L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenFindRoleByIdAsUserThenReturnStatusIsForbidden() throws Exception {
        mvc.perform(get("/api/roles/{id}", 1L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testWhenFindRoleByIdAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        mvc.perform(get("/api/roles/{id}", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //UPDATE ROLE BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateRoleByIdAsAdminThenReturnStatusIsNoContent() throws Exception {
        RoleDTO dto = new RoleDTO("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/api/roles/{id}", 2L).content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateRoleByIdAsAdminThenReturnStatusIsBadRequest() throws Exception {
        RoleDTO dto = new RoleDTO();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/api/roles/{id}", 2L).content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateRoleByIdAsAdminThenReturnStatusIsForbidden() throws Exception {
        RoleDTO dto = new RoleDTO("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/api/roles/{id}", 1L).content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenUpdateRoleByIdAsAdminThenReturnStatusIsNotFound() throws Exception {
        RoleDTO dto = new RoleDTO("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/api/roles/{id}", 3L).content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenUpdateRoleByIdAsUserThenReturnStatusIsForbidden() throws Exception {
        RoleDTO dto = new RoleDTO("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/api/roles/{id}", 2L).content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testWhenUpdateRoleByIdAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        RoleDTO dto = new RoleDTO("ROLE_MODERATOR");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/api/roles/{id}", 2L).content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //DELETE ROLE BY ID
    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenDeleteRoleByIdAsAdminThenReturnStatusIsNoContent() throws Exception {
        mvc.perform(delete("/api/roles/{id}", 2L))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenDeleteRoleByIdAsAdminThenReturnStatusIsNotFound() throws Exception {
        mvc.perform(delete("/api/roles/{id}", 3L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testWhenDeleteRoleByIdAsAdminThenReturnStatusIsForbidden() throws Exception {
        mvc.perform(delete("/api/roles/{id}", 1L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWhenDeleteRoleByIdAsUserThenReturnStatusIsForbidden() throws Exception {
        mvc.perform(delete("/api/roles/{id}", 2L))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testWhenDeleteRoleByIdAsAnonymousUserThenReturnStatusIsUnauthorized() throws Exception {
        mvc.perform(delete("/api/roles/{id}", 2L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
