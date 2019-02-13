package pl.medm.javadev.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.medm.javadev.model.dto.LectureDTO;
import pl.medm.javadev.model.dto.RoleDTO;
import pl.medm.javadev.model.dto.UserDTO;
import pl.medm.javadev.model.entity.Lecture;
import pl.medm.javadev.model.entity.Role;
import pl.medm.javadev.model.entity.User;
import pl.medm.javadev.repository.RoleRepository;
import pl.medm.javadev.repository.UserRepository;
import pl.medm.javadev.utils.exception.ConflictException;
import pl.medm.javadev.utils.exception.NotFoundException;
import pl.medm.javadev.utils.mapper.LectureMapper;
import pl.medm.javadev.utils.mapper.RoleMapper;
import pl.medm.javadev.utils.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private LectureMapper lectureMapper = Mappers.getMapper(LectureMapper.class);

    private RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    private UserService userService;

    @BeforeEach
    void setup() {
        this.userService = new UserService(userRepository, roleRepository, passwordEncoder, userMapper, lectureMapper, roleMapper);
    }

    //FIND ALL USERS
    @Test
    void testWhenFindAllUsersThenUsersFound() {
        List<User> users = Arrays.asList(
                new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                        "1", "Automatics", "000001"),
                new User(2L, "Steven", "Rogers", "capitan.america@marvel.com", "zaq1@WSX",
                        "2", "Electronics", "000002")
        );
        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> expected = users.stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
        List<UserDTO> actual = userService.findAllUsers();

        Assertions.assertIterableEquals(expected, actual);
        verify(userRepository, times(1)).findAll();
    }

    //CREATE USER
    @Test
    void testWhenCreateUserThenUserCreated() {
        UserDTO dto = new UserDTO("Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        User user = new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "encrypted password",
                "1", "Automatics", "000001");
        Role role = new Role(2L, "USER_ROLE");
        user.getRoles().add(role);
        when(userRepository.existsByEmailOrIndexNumber("hawkeye@marvel.com", "000001")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(roleRepository.findByRole(anyString())).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("encrypted password");

        UserDTO actual = userService.createUser(dto);

        Assertions.assertEquals(1L, actual.getId().longValue());
        Assertions.assertEquals("Clint", actual.getFirstName());
        Assertions.assertEquals("Barton", actual.getLastName());
        Assertions.assertEquals("hawkeye@marvel.com", actual.getEmail());
        Assertions.assertEquals("encrypted password", actual.getPassword());
        Assertions.assertEquals("1", actual.getYearOfStudy());
        Assertions.assertEquals("Automatics", actual.getFieldOfStudy());
        Assertions.assertEquals("000001", actual.getIndexNumber());
        Assertions.assertEquals(1, actual.getRoles().size());
        Assertions.assertTrue(actual.getRoles().contains(new RoleDTO(2L, "USER_ROLE")));
        verify(userRepository, times(1)).existsByEmailOrIndexNumber("hawkeye@marvel.com", "000001");
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).findByRole(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    void testWhenCreateUserThenUserConflict() {
        UserDTO dto = new UserDTO("Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        when(userRepository.existsByEmailOrIndexNumber("hawkeye@marvel.com", "000001")).thenReturn(true);

        Throwable exception = assertThrows(ConflictException.class, () ->
                userService.createUser(dto)
        );

        Assertions.assertEquals("Conflict! Email hawkeye@marvel.com or index number 000001 is already busy.", exception.getMessage());
        verify(userRepository, times(1)).existsByEmailOrIndexNumber("hawkeye@marvel.com", "000001");
        verify(userRepository, times(0)).save(any(User.class));
    }

    //FIND USER BY ID
    @Test
    void testWhenFindUserByIdThenUserFound() {
        User user = new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO expected = userMapper.userToUserDTO(user);
        UserDTO actual = userService.findUserById(1L);

        Assertions.assertEquals(expected, actual);
        verify(userRepository, times(1)).findById(1L);

    }

    @Test
    void testWhenFindUserByIdThenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class, () ->
                userService.findUserById(1L)
        );

        Assertions.assertEquals("User not found!", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    //UPDATE USER BY ID
    @Test
    void testWhenUpdateUserByIdThenUserUpdated() {
        User user = new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        UserDTO updated = new UserDTO(1L, "Bruce", "Banner", "hulk@marvel.com", null,
                "3", "Informatics", "000003");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailOrIndexNumber("hulk@marvel.com", "000003")).thenReturn(false);

        userService.updateUserById(1L, updated);
        UserDTO actual = userService.findUserById(1L);

        Assertions.assertEquals(1L, actual.getId().longValue());
        Assertions.assertEquals("Bruce", actual.getFirstName());
        Assertions.assertEquals("Banner", actual.getLastName());
        Assertions.assertEquals("hulk@marvel.com", actual.getEmail());
        Assertions.assertEquals("3", actual.getYearOfStudy());
        Assertions.assertEquals("Informatics", actual.getFieldOfStudy());
        Assertions.assertEquals("000003", actual.getIndexNumber());
        verify(userRepository, times(2)).findById(1L);
        verify(userRepository, times(1)).existsByEmailOrIndexNumber("hulk@marvel.com", "000003");
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    void testWhenUpdateUserByIdThenUserNotFound() {
        UserDTO updated = new UserDTO(1L, "Bruce", "Banner", "hulk@marvel.com", null,
                "3", "Informatics", "000003");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class, () ->
                userService.updateUserById(1L, updated)
        );

        Assertions.assertEquals("User not found!", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(0)).existsByEmailOrIndexNumber("hulk@marvel.com", "000003");
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void testWhenUpdateUserByIdThenUserConflict() {
        User user = new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        UserDTO updated = new UserDTO(1L, "Bruce", "Banner", "hulk@marvel.com", null,
                "3", "Informatics", "000003");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailOrIndexNumber("hulk@marvel.com", "000003")).thenReturn(true);

        Throwable exception = assertThrows(ConflictException.class, () ->
                userService.updateUserById(1L, updated)
        );

        Assertions.assertEquals("Conflict! Email hulk@marvel.com or index number 000003 is already busy.", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmailOrIndexNumber("hulk@marvel.com", "000003");
        verify(userRepository, times(0)).save(any(User.class));
    }

    //UPDATE USER PASSWORD BY ID
    @Test
    void testWhenUpdateUserPasswordByIdThenUserPasswordUpdated() {
        User user = new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        UserDTO updated = new UserDTO(1L, "xsw2!QAZ");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateUserPasswordById(1L, updated);
        UserDTO actual = userService.findUserById(1L);

        Assertions.assertEquals(1L, actual.getId().longValue());
        Assertions.assertEquals("xsw2!QAZ", actual.getPassword());
        verify(userRepository, times(2)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testWhenUpdateUserPasswordByIdThenUserNotFound() {
        UserDTO updated = new UserDTO(1L, "xsw2!QAZ");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class, () ->
                userService.updateUserById(1L, updated)
        );

        Assertions.assertEquals("User not found!", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(0)).save(any(User.class));
    }

    //DELETE USER BY ID
    @Test
    void testWhenDeleteUserByIdThenUserDeleted() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testWhenDeleteUserByIdThenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        Throwable exception = assertThrows(NotFoundException.class, () ->
                userService.deleteUserById(1L)
        );

        Assertions.assertEquals("User not found!", exception.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(0)).deleteById(1L);
    }

    //FIND ALL USER LECTURES
    @Test
    void testWhenFindAllUserLecturesThenUserLecturesFound() {
        User user = new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        user.getLectures().addAll(Arrays.asList(
                new Lecture(1L, "Java 8", "The basics of language", "Howard Stark", true),
                new Lecture(2L, "Spring", "The basics of framework", "Howard Stark", false)
        ));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<LectureDTO> actual = userService.findAllUserLecturesById(1L);

        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals(1L, actual.get(0).getId().longValue());
        Assertions.assertEquals("Java 8", actual.get(0).getTitle());
        Assertions.assertEquals("The basics of language", actual.get(0).getDescription());
        Assertions.assertEquals("Howard Stark", actual.get(0).getLecturer());
        Assertions.assertEquals(2L, actual.get(1).getId().longValue());
        Assertions.assertEquals("Spring", actual.get(1).getTitle());
        Assertions.assertEquals("The basics of framework", actual.get(1).getDescription());
        Assertions.assertEquals("Howard Stark", actual.get(1).getLecturer());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testWhenFindAllUserLecturesThenUserLecturesNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class, () ->
                userService.findAllUserLecturesById(1L)
        );

        Assertions.assertEquals("User not found!", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }
}