package pl.medm.javadev.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.medm.javadev.model.entity.Lecture;
import pl.medm.javadev.model.entity.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
    private ObjectMapper mapper = new ObjectMapper();

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

    @ParameterizedTest
    @MethodSource("lectureProvider")
    @WithMockUser(roles = "ADMIN")
    void testCreateLectureForAdmin(String title, String description, String lecturer, Boolean completed,
                                   List<ResultMatcher> statuses) throws Exception {
        Lecture lecture = new Lecture(title, description, lecturer, completed);
        for (ResultMatcher status : statuses) {
            mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status);
        }
    }

    @ParameterizedTest
    @MethodSource("lectureProvider")
    @WithMockUser(roles = "USER")
    void testCreateLectureForUser(String title, String description, String lecturer, Boolean completed) throws Exception {
        Lecture lecture = new Lecture(title, description, lecturer, completed);
        mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("lectureProvider")
    @WithAnonymousUser
    void testCreateLectureForAnonymousUser(String title, String description, String lecturer, Boolean completed) throws Exception {
        Lecture lecture = new Lecture(title, description, lecturer, completed);
        mvc.perform(post("/lectures").content(mapper.writeValueAsString(lecture)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    private static Stream<Arguments> lectureProvider() {
        ResultMatcher http_201 = status().isCreated();
        ResultMatcher http_400 = status().isBadRequest();
        ResultMatcher http_409 = status().isConflict();
        ResultMatcher http_402 = status().isUnauthorized();
        ResultMatcher http_403 = status().isForbidden();
        ResultMatcher http_404 = status().isNotFound();
        String title = "Java 8";
        String smallTitle = "CSS";
        String bigTitle = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt " +
                "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris" +
                " nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit " +
                "esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in" +
                " culpa qui officia deserunt mollit anim id est laborum.";
        String description = "The basics of language";
        String bigDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation " +
                "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non " +
                "proident, sunt in culpa qui officia deserunt mollit anim id est laborum." +
                "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, " +
                "totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae " +
                "dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, " +
                "sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam " +
                "est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius " +
                "modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima " +
                "veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea " +
                "commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil" +
                " molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?";
        String lecturer = "Tony Stark";
        String smallLecturer = "Li";
        String bigLecturer = "Aleksandra Maria Konstantynopolitanczykóweczka";
        return Stream.of(
                Arguments.of(null, null, null, null, Arrays.asList(http_400, http_400)),
                Arguments.of(null, null, null, false, Arrays.asList(http_400, http_400)),
                Arguments.of(null, null, lecturer, null, Arrays.asList(http_400, http_400)),
                Arguments.of(null, null, lecturer, true, Arrays.asList(http_400, http_400)),
                Arguments.of(null, description, null, null, Arrays.asList(http_400, http_400)),
                Arguments.of(null, description, null, false, Arrays.asList(http_400, http_400)),
                Arguments.of(null, description, lecturer, null, Arrays.asList(http_400, http_400)),
                Arguments.of(null, description, lecturer, true, Arrays.asList(http_400, http_400)),
                Arguments.of(title, null, null, null, Arrays.asList(http_400, http_400)),
                Arguments.of(title, null, null, false, Arrays.asList(http_400, http_400)),
                Arguments.of(title, null, lecturer, true, Arrays.asList(http_201, http_409)),
                Arguments.of(title, description, null, null, Arrays.asList(http_400, http_400)),
                Arguments.of(title, description, null, false, Arrays.asList(http_400, http_400)),
                Arguments.of(title, description, lecturer, null, Arrays.asList(http_201, http_409)),
                Arguments.of(title, description, lecturer, true, Arrays.asList(http_201, http_409)),
                Arguments.of(smallTitle, description, lecturer, false, Arrays.asList(http_400, http_400)),
                Arguments.of(bigTitle, description, lecturer, true, Arrays.asList(http_400, http_400)),
                Arguments.of(title, bigDescription, lecturer, true, Arrays.asList(http_400, http_400)),
                Arguments.of(title, description, smallLecturer, false, Arrays.asList(http_400, http_400)),
                Arguments.of(title, description, bigLecturer, true, Arrays.asList(http_400, http_400))
        );
    }

    @ParameterizedTest
    @MethodSource("lectureProviderForFindLectureById")
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-test.sql")
    void testFindLectureByIdForAdmin(Long id, ResultMatcher status) throws Exception {
        mvc.perform(get("/lectures/{id}", id))
                .andDo(print())
                .andExpect(status);
    }

    @ParameterizedTest
    @MethodSource("lectureProviderForFindLectureById")
    @WithMockUser(roles = "USER")
    @Sql("/data-test.sql")
    void testFindLectureByIdForUser(Long id, ResultMatcher status) throws Exception {
        mvc.perform(get("/lectures/{id}", id))
                .andDo(print())
                .andExpect(status);
    }

    @ParameterizedTest
    @MethodSource("lectureProviderForFindLectureById")
    @WithAnonymousUser
    void testFindLectureByIdForAnonymousUser(Long id) throws Exception {
        mvc.perform(get("/lectures/{id}", id))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private static Stream<Arguments> lectureProviderForFindLectureById() {
        ResultMatcher http_200 = status().isOk();
        ResultMatcher http_404 = status().isNotFound();
        return Stream.of(
                Arguments.of(1L, http_200),
                Arguments.of(2L, http_200),
                Arguments.of(3L, http_404),
                Arguments.of(4L, http_404),
                Arguments.of(5L, http_404),
                Arguments.of(6L, http_404),
                Arguments.of(7L, http_404),
                Arguments.of(8L, http_404),
                Arguments.of(9L, http_404),
                Arguments.of(10L, http_404),
                Arguments.of(11L, http_404),
                Arguments.of(12L, http_404),
                Arguments.of(13L, http_404),
                Arguments.of(14L, http_404),
                Arguments.of(15L, http_404),
                Arguments.of(16L, http_404),
                Arguments.of(17L, http_404),
                Arguments.of(18L, http_404),
                Arguments.of(19L, http_404),
                Arguments.of(20L, http_404)
        );
    }

    @ParameterizedTest
    @MethodSource("lectureProviderForUpdateLectureById")
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-test.sql")
    void testUpdateLectureForAdmin(String title, String description, String lecturer, Boolean completed,
                                   List<ResultMatcher> statuses) throws Exception {
        Lecture updated = new Lecture(title, description, lecturer, completed);
        ObjectMapper mapper = new ObjectMapper();
        long id = 2L;
        for (ResultMatcher status : statuses) {
            mvc.perform(put("/lectures/{id}", id++).content(mapper.writeValueAsString(updated)).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status);
        }
    }

    @ParameterizedTest
    @MethodSource("lectureProviderForUpdateLectureById")
    @WithMockUser(roles = "USER")
    @Sql("/data-test.sql")
    void testUpdateLectureForUser(String title, String description, String lecturer, Boolean completed) throws Exception {
        Lecture updated = new Lecture(title, description, lecturer, completed);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(updated)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("lectureProviderForUpdateLectureById")
    @WithAnonymousUser
    @Sql("/data-test.sql")
    void testUpdateLectureForAnonymousUser(String title, String description, String lecturer, Boolean completed) throws Exception {
        Lecture updated = new Lecture(title, description, lecturer, completed);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(put("/lectures/{id}", 1L).content(mapper.writeValueAsString(updated)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private static Stream<Arguments> lectureProviderForUpdateLectureById() {
        ResultMatcher http_204 = status().isNoContent();
        ResultMatcher http_400 = status().isBadRequest();
        ResultMatcher http_404 = status().isNotFound();
        String title = "Java 8";
        String smallTitle = "CSS";
        String bigTitle = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt " +
                "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris" +
                " nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit " +
                "esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in" +
                " culpa qui officia deserunt mollit anim id est laborum.";
        String description = "The basics of language";
        String bigDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation " +
                "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non " +
                "proident, sunt in culpa qui officia deserunt mollit anim id est laborum." +
                "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, " +
                "totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae " +
                "dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, " +
                "sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam " +
                "est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius " +
                "modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima " +
                "veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea " +
                "commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil" +
                " molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?";
        String lecturer = "Tony Stark";
        String smallLecturer = "Li";
        String bigLecturer = "Aleksandra Maria Konstantynopolitanczykóweczka";
        return Stream.of(
                Arguments.of(null, null, null, null, Arrays.asList(http_400, http_400)),
                Arguments.of(null, null, null, false, Arrays.asList(http_400, http_400)),
                Arguments.of(null, null, lecturer, null, Arrays.asList(http_400, http_400)),
                Arguments.of(null, null, lecturer, true, Arrays.asList(http_400, http_400)),
                Arguments.of(null, description, null, null, Arrays.asList(http_400, http_400)),
                Arguments.of(null, description, null, false, Arrays.asList(http_400, http_400)),
                Arguments.of(null, description, lecturer, null, Arrays.asList(http_400, http_400)),
                Arguments.of(null, description, lecturer, true, Arrays.asList(http_400, http_400)),
                Arguments.of(title, null, null, null, Arrays.asList(http_400, http_400)),
                Arguments.of(title, null, null, false, Arrays.asList(http_400, http_400)),
                Arguments.of(title, null, lecturer, true, Arrays.asList(http_204, http_404)),
                Arguments.of(title, description, null, null, Arrays.asList(http_400, http_400)),
                Arguments.of(title, description, null, false, Arrays.asList(http_400, http_400)),
                Arguments.of(title, description, lecturer, null, Arrays.asList(http_204, http_404)),
                Arguments.of(title, description, lecturer, true, Arrays.asList(http_204, http_404)),
                Arguments.of(smallTitle, description, lecturer, false, Arrays.asList(http_400, http_400)),
                Arguments.of(bigTitle, description, lecturer, true, Arrays.asList(http_400, http_400)),
                Arguments.of(title, bigDescription, lecturer, true, Arrays.asList(http_400, http_400)),
                Arguments.of(title, description, smallLecturer, false, Arrays.asList(http_400, http_400)),
                Arguments.of(title, description, bigLecturer, true, Arrays.asList(http_400, http_400))
        );
    }

    @ParameterizedTest
    @MethodSource("lectureProviderForDeleteLectureById")
    @WithMockUser(roles = "ADMIN")
    @Sql("/data-test.sql")
    void testDeleteLectureForAdmin(Long id, ResultMatcher status) throws Exception {
        mvc.perform(delete("/lectures/{id}", id))
                .andDo(print())
                .andExpect(status);
    }

    @ParameterizedTest
    @MethodSource("lectureProviderForDeleteLectureById")
    @WithMockUser(roles = "USER")
    @Sql("/data-test.sql")
    void testDeleteLectureForUser(Long id) throws Exception {
        mvc.perform(delete("/lectures/{id}", id))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("lectureProviderForDeleteLectureById")
    @WithAnonymousUser
    @Sql("/data-test.sql")
    void testDeleteLectureForAnonymousUser(Long id) throws Exception {
        mvc.perform(delete("/lectures/{id}", id))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private static Stream<Arguments> lectureProviderForDeleteLectureById() {
        ResultMatcher http_204 = status().isNoContent();
        ResultMatcher http_403 = status().isForbidden();
        ResultMatcher http_404 = status().isNotFound();
        return Stream.of(
                Arguments.of(1L, http_403),
                Arguments.of(2L, http_204),
                Arguments.of(3L, http_404),
                Arguments.of(4L, http_404),
                Arguments.of(5L, http_404),
                Arguments.of(6L, http_404),
                Arguments.of(7L, http_404),
                Arguments.of(8L, http_404),
                Arguments.of(9L, http_404),
                Arguments.of(10L, http_404),
                Arguments.of(11L, http_404),
                Arguments.of(12L, http_404),
                Arguments.of(13L, http_404),
                Arguments.of(14L, http_404),
                Arguments.of(15L, http_404),
                Arguments.of(16L, http_404),
                Arguments.of(17L, http_404),
                Arguments.of(18L, http_404),
                Arguments.of(19L, http_404),
                Arguments.of(20L, http_404)
        );
    }

    @ParameterizedTest
    @MethodSource("lectureProviderForAddUserToLecture")
    @WithMockUser(roles = "USER")
    @Sql("/data-test.sql")
    void testAddUserTOLectureForAdmin(Long id, User user, ResultMatcher status) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(get("/lectures")).andDo(print());
        mvc.perform(get("/lectures")).andDo(print());
        mvc.perform(post("/lectures/{id}/users", id).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status);
    }

    private static Stream<Arguments> lectureProviderForAddUserToLecture() {
        ResultMatcher http_201 = status().isCreated();
        ResultMatcher http_204 = status().isNoContent();
        ResultMatcher http_400 = status().isBadRequest();
        ResultMatcher http_403 = status().isForbidden();
        ResultMatcher http_404 = status().isNotFound();
        User userId1 = new User(1L, "James", "Bond", "agent007@uk.com", "{noop}zaq1@WSX");
        User userId2 = new User(2L, "Steven", "Rogers", "capitan.ameryka@marvel.com", "{noop}zaq1@WSX");
        return Stream.of(
                Arguments.of(1L, userId1, http_201),
                Arguments.of(2L, userId1, http_201),//2
                Arguments.of(3L, userId1, http_404),
                Arguments.of(4L, userId1, http_404),
                Arguments.of(5L, userId1, http_404),
                Arguments.of(1L, userId2, http_201),//6
                Arguments.of(2L, userId2, http_201),//7
                Arguments.of(3L, userId2, http_404),
                Arguments.of(4L, userId2, http_404),
                Arguments.of(5L, userId2, http_404),
                Arguments.of(1L, null, http_400),
                Arguments.of(2L, null, http_400),
                Arguments.of(3L, null, http_400)
        );
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
    @Sql("/data-test.sql")
    void testSaveUserTOLectureForUserWhenLectureNotFoundUserFound() throws Exception {
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/lectures/{id}/users", 1L).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
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
