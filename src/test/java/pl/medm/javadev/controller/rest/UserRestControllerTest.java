package pl.medm.javadev.controller.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.medm.javadev.model.dto.TestUserDTO;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;;

@ActiveProfiles("test")
@SpringBootTest
public class UserRestControllerTest {

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
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "user");
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testGetAllUsersForAdmin() throws Exception {
        mvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void testGetAllUsersForUser() throws Exception {
        mvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetAllUsersForAnonymousUser() throws Exception {
        mvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testCreateUserForAdminWhenIsOk() throws Exception {
        TestUserDTO body = new TestUserDTO(null, "Jan", "Nowak", "jannowak@gmail.com", "zaq1@WSX");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("!!!" + mapper.writeValueAsString(body));
        mvc.perform(post("/users").content(mapper.writeValueAsString(body)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();
    }

//    @Test
//    @WithMockUser(authorities = {"ROLE_ADMIN"})
//    void testCreateUserForAdminWhen() throws Exception {
//        TestUserDTO body = new TestUserDTO(null, null, "Nowak", "jannowak@gmail.com", "zaq1@WSX");
//        ObjectMapper mapper = new ObjectMapper();
//        System.out.println("!!!" + mapper.writeValueAsString(body));
//        mvc.perform(post("/users").content(mapper.writeValueAsString(body)).contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").isNotEmpty())
//                .andReturn();
//    }

    @ParameterizedTest
    @MethodSource("userProvider")
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testCreateUserForAdmin(Long id, String firstName, String lastName, String email, String password, ResultMatcher status1,
                                ResultMatcher status2) throws Exception {
        TestUserDTO body = new TestUserDTO(id, firstName, lastName, email, password);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status1);
        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status2);
    }

    private static Stream<Arguments> userProvider() {
        ResultMatcher http_201 = status().isCreated();
        ResultMatcher http_400 = status().isBadRequest();
        ResultMatcher http_409 = status().isConflict();
        return Stream.of(
                Arguments.of(null, "Jan", "Nowak", "jannowak@gmail.com", "zaq1@WSX", http_201, http_409),
                Arguments.of(null, null, "Nowak", "jannowak@gmail.com", "zaq1@WSX", http_400, http_400),
                Arguments.of(null, "Jan", null, "jannowak@gmail.com", "zaq1@WSX", http_400, http_400),
                Arguments.of(null, "Jan", "Nowak", null, "zaq1@WSX", http_400, http_400),
                Arguments.of(null, "Jan", "Nowak", "jannowak@gmail.com", null, http_400, http_400),
                Arguments.of(null, "Jan", "Nowak", "jannowak", "zaq1@WSX", http_400, http_400),
                Arguments.of(null, "Jan", "Nowak", "jannowak@gmail.com", "zaq", http_400, http_400),
                Arguments.of(null, "Jan", "Nowak", "jannowak@gmail.com", "zaq1", http_400, http_400),
                Arguments.of(null, "Jan", "Nowak", "jannowak@gmail.com", "zaq1@", http_400, http_400)
        );
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void testCreateUserForUser() throws Exception {
        TestUserDTO body = new TestUserDTO(null, "Jan", "Nowak", "jannowak@gmail.com", "zaq1@WSX");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/users").content(mapper.writeValueAsString(body)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    void testCreateUserForAnonymousUser() throws Exception {
        TestUserDTO body = new TestUserDTO(null, "Jan", "Nowak", "jannowak@gmail.com", "zaq1@WSX");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/users").content(mapper.writeValueAsString(body)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testFindUserByIdForAdminWhenNotFound() throws Exception {
        Long id = 1L;
        mvc.perform(get("/user/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testFindUserByIdForUserWhenNotFound() throws Exception {
        Long id = 1L;
        mvc.perform(get("/user/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testFindUserByIdForAnonymousUserWhenNotFound() throws Exception {
        Long id = 1L;
        mvc.perform(get("/user/{id}", id))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testFindUserByIdForAdminWhenFound() throws Exception {
        MvcResult resultPost = createUser();
        String location = resultPost.getResponse().getHeader("Location");
        Assertions.assertNotNull(location);
        mvc.perform(get(location))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(authorities = {"ROLE_ADMIN"})
    private MvcResult createUser() throws Exception {
        TestUserDTO body = new TestUserDTO(null, "Jan", "Nowak", "jannowak@gmail.com", "zaq1@WSX");
        ObjectMapper mapper = new ObjectMapper();
        return mvc.perform(post("/users").content(mapper.writeValueAsString(body)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void testFindUserByIdForUserWhenFound() throws Exception {
        MvcResult resultPost = createUser();
        String location = resultPost.getResponse().getHeader("Location");
        Assertions.assertNotNull(location);
        mvc.perform(get(location))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @RepeatedTest(10)
    @WithAnonymousUser
    void testFindUserByIdForAnonymousUserWhenFound() throws Exception {
        mvc.perform(get("/users/{id}", new Random().nextInt(100)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //PUT all user
    @ParameterizedTest
    @MethodSource("argumentsToUpdate")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testUpdateUserDataForAdmin(Long id, String firstName, String lastName, String email, String password,
                                    String yearOfStudy, String fieldOfStudy, String indexNumber,
                                    ResultMatcher status) throws Exception {
        TestUserDTO body = new TestUserDTO(null, firstName, lastName, email, null, yearOfStudy, fieldOfStudy,
                indexNumber);
        ObjectMapper mapper = new ObjectMapper();
        MvcResult resultPost = createUser();
        String location = resultPost.getResponse().getHeader("Location");
        Assertions.assertNotNull(location);
        mvc.perform(put(location).content(mapper.writeValueAsString(body)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status);
    }

    private static Stream<Arguments> argumentsToUpdate() {
        ResultMatcher http_204 = status().isNoContent();
        ResultMatcher http_400 = status().isBadRequest();
        return Stream.of(
                Arguments.of(null, "Karol", "Wicher", "karolwicher@gmail.com", null, "1", "automatyka", "123456", http_204),
                Arguments.of(null, "Karol", "Wicher", "karolwicher@gmail.com", null, "1", "automatyka", null, http_204),
                Arguments.of(null, "Karol", "Wicher", "karolwicher@gmail.com", null, "1", null, null, http_204),
                Arguments.of(null, "Karol", "Wicher", "karolwicher@gmail.com", null, null, null, null, http_204),
                Arguments.of(null, "Karol", "Wicher", "karolwicher", null, "1", "automatyka", "123456", http_400),
                Arguments.of(null, "Karol", null, "karolwicher@gmail.com", null, "1", "automatyka", "123456", http_400),
                Arguments.of(null, null, "Wicher", "karolwicher@gmail.com", null, "1", "automatyka", "123456", http_400)
        );
    }



        //PUT password
    @ParameterizedTest
    @MethodSource("passwordProvider")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testUpdateUserPasswordForAdmin(String password, ResultMatcher status) throws Exception {
        testUpdateUserPassword(password, status);
    }

    @ParameterizedTest
    @MethodSource("passwordProvider")
    @WithMockUser(authorities = "ROLE_USER")
    void testUpdateUserPasswordForUser(String password, ResultMatcher status) throws Exception {
        testUpdateUserPassword(password, status);
    }

    @ParameterizedTest
    @MethodSource("passwordProvider")
    @WithAnonymousUser
    void testUpdateUserPasswordForAnonymousUser(String password) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/users/1/password").content(mapper.writeValueAsString(password)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
//        testUpdateUserPassword(password, status);
    }

    private void testUpdateUserPassword(String password, ResultMatcher status) throws Exception {
        TestUserDTO body = new TestUserDTO(null, null, null, null, password);
        ObjectMapper mapper = new ObjectMapper();
        MvcResult resultPost = createUser();
        String location = resultPost.getResponse().getHeader("Location");
        System.out.println(location);
        Assertions.assertNotNull(location);
        mvc.perform(put(location + "/password").content(mapper.writeValueAsString(body)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status);
    }

    private static Stream<Arguments> passwordProvider() {
        ResultMatcher http_204 = status().isNoContent();
        ResultMatcher http_400 = status().isBadRequest();
        return Stream.of(
                Arguments.of("vfrvfrvf", http_400),
                Arguments.of("vfrtVFRT", http_400),
                Arguments.of("vfr45TGB", http_400),
                Arguments.of("vfr4@TGB", http_204),
                Arguments.of("aa", http_400),
                Arguments.of("vfr4@TGBdasdawdqASDASDew2t31134!@21asda", http_400)
        );
    }

    //DELTETE
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testDeleteUserByIdForAdmin() throws Exception {
        MvcResult resultPost = createUser();
        String location = resultPost.getResponse().getHeader("Location");
        Assertions.assertNotNull(location);
        mvc.perform(get(location))
                .andExpect(status().isOk());
        mvc.perform(delete(location))
                .andDo(print())
                .andExpect(status().isNoContent());
        mvc.perform(delete(location))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @RepeatedTest(10)
    @WithMockUser(authorities = {"ROLE_USER"})
    void testDeleteUserByIdForUser() throws Exception {
        mvc.perform(delete("/users/{id}", new Random().nextInt(100)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @RepeatedTest(10)
    @WithAnonymousUser
    void testDeleteUserByIdForAnonymousUser() throws Exception {
        mvc.perform(delete("/users/{id}", new Random().nextInt(100)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    //PUT /users/id/lecture
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testFindAllLecturesByUserIdForAdmin() throws Exception {
        jdbcTemplate.execute(sqlCreateUser);
        jdbcTemplate.execute(sqlCreateLecture);
//        jdbcTemplate.execute();
        mvc.perform(get("/users"))
                .andDo(print());
    }

    private String sqlCreateUser = "INSERT INTO user (first_Name, last_Name, email, password)" +
            "VALUES ('Jan', 'Kowalski', 'jankowalski@gmail.com', 'zaq1@WSX')";
    private String sqlCreateLecture = "INSERT INTO lecture (title, description, lecturer, completed)" +
            "VALUES ('Spring boot', 'Wprowadzenie do spring boot', 'Jan Mucha', 'false')";
}
