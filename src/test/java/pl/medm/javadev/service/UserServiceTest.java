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
import pl.medm.javadev.model.dto.UserDTO;
import pl.medm.javadev.model.dto.UserPasswordDTO;
import pl.medm.javadev.model.entity.Lecture;
import pl.medm.javadev.model.entity.User;
import pl.medm.javadev.repository.RoleRepository;
import pl.medm.javadev.repository.UserRepository;
import pl.medm.javadev.utils.exception.ConflictException;
import pl.medm.javadev.utils.exception.NotFoundException;
import pl.medm.javadev.utils.mapper.LectureMapper;
import pl.medm.javadev.utils.mapper.UserMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    private UserService userService;

    @BeforeEach
    void setup() {
        this.userService = new UserService(userRepository, roleRepository, passwordEncoder, userMapper, lectureMapper);
    }

    //FIND ALL USERS
    @Test
    void testWhenFindAllUsersThenUsersFound() {
        List<User> users = Arrays.asList(
                new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                        "1", "Automatics", "000001"),
                new User(2, "Steven", "Rogers", "capitan.america@marvel.com", "zaq1@WSX",
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
        User user = new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        when(userRepository.existsByEmailOrIndexNumber("hawkeye@marvel.com", "000001")).thenReturn(false);

        UserDTO expected = userMapper.userToUserDTO(user);
        UserDTO actual = userService.createUser(user);

        Assertions.assertEquals(expected, actual);
        verify(userRepository, times(1)).existsByEmailOrIndexNumber("hawkeye@marvel.com", "000001");
        verify(userRepository, times(1)).save(user);

    }

    @Test
    void testWhenCreateUserThenUserConflict() {
        User user = new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        when(userRepository.existsByEmailOrIndexNumber("hawkeye@marvel.com", "000001")).thenReturn(true);

        Throwable exception = assertThrows(ConflictException.class, () ->
                userService.createUser(user)
        );

        Assertions.assertEquals("User conflict!", exception.getMessage());
        verify(userRepository, times(1)).existsByEmailOrIndexNumber("hawkeye@marvel.com", "000001");
        verify(userRepository, times(0)).save(user);
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
        User updated = new User(1L, "Bruce", "Banner", "hulk@marvel.com", null,
                "3", "Informatics", "000003");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailOrIndexNumber("hulk@marvel.com", "000003")).thenReturn(false);

        userService.updateUserById(1L, updated);
        UserDTO expected = userMapper.userToUserDTO(updated);
        UserDTO actual = userService.findUserById(1L);

        Assertions.assertEquals(expected, actual);
        verify(userRepository, times(2)).findById(1L);
        verify(userRepository, times(1)).existsByEmailOrIndexNumber("hulk@marvel.com", "000003");
        verify(userRepository, times(1)).save(user);

    }

    @Test
    void testWhenUpdateUserByIdThenUserNotFound() {
        User user = new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        User updated = new User(1L, "Bruce", "Banner", "hulk@marvel.com", null,
                "3", "Informatics", "000003");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class, () ->
                userService.updateUserById(1L, updated)
        );

        Assertions.assertEquals("User not found!", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(0)).existsByEmailOrIndexNumber("hulk@marvel.com", "000003");
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void testWhenUpdateUserByIdThenUserConflict() {
        User user = new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        User updated = new User(1L, "Bruce", "Banner", "hulk@marvel.com", null,
                "3", "Informatics", "000003");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailOrIndexNumber("hulk@marvel.com", "000003")).thenReturn(true);

        Throwable exception = assertThrows(ConflictException.class, () ->
                userService.updateUserById(1L, updated)
        );

        Assertions.assertEquals("User conflict!", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmailOrIndexNumber("hulk@marvel.com", "000003");
        verify(userRepository, times(0)).save(user);
    }

    //UPDATE USER PASSWORD BY ID
    @Test
    void testWhenUpdateUserPasswordByIdThenUserPasswordUpdated() {
        User user = new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        User updated = new User(1L, null, null, null, "xsw2!QAZ",
                null, null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateUserPasswordById(1L, updated);
        UserPasswordDTO expected = userMapper.userToUserPasswordDTO(updated);
        UserPasswordDTO actual = userService.findUserPasswordById(1L);

        Assertions.assertEquals(expected, actual);
        verify(userRepository, times(2)).findById(1L);
        verify(userRepository, times(1)).save(user);

    }

    @Test
    void testWhenUpdateUserPasswordByIdThenUserNotFound() {
        User user = new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        User updated = new User(1L, null, null, null, "xsw2!QAZ",
                null, null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class, () ->
                userService.updateUserById(1L, updated)
        );

        Assertions.assertEquals("User not found!", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(0)).save(user);
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
        List<Lecture> lectures = Arrays.asList(
                new Lecture(1L, "Java 8", "The basics of language", "Tony Stark", true),
                new Lecture(2L, "Spring", "The basics of framework", "Bruce Banner", false)
        );
        user.getLectures().addAll(lectures);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<LectureDTO> expected = lectures.stream()
                .map(lectureMapper::lectureToLectureDTO)
                .collect(Collectors.toList());
        List<LectureDTO> actual = userService.findAllUserLecturesById(1L);

        Assertions.assertEquals(expected, actual);
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