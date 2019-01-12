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
import pl.medm.javadev.model.entity.Lecture;
import pl.medm.javadev.model.entity.User;
import pl.medm.javadev.repository.RoleRepository;
import pl.medm.javadev.repository.UserRepository;
import pl.medm.javadev.utils.exception.UserExistsException;
import pl.medm.javadev.utils.exception.UserNotFoundException;
import pl.medm.javadev.utils.mapper.LectureMapper;
import pl.medm.javadev.utils.mapper.UserMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Mock

    private UserService userService;

    @BeforeEach
    void setup() {
        this.userService = new UserService(userRepository, roleRepository, passwordEncoder, userMapper, lectureMapper);
    }

    @Test
    void testFindAllUser() {
        User user1 = new User(1L, "Tony", "Stark", "ironman@gmail.com", "zaq1@WSX");
        User user2 = new User(2L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        List<User> users = Arrays.asList(user1, user2);
        List<UserDTO> expected = users.stream().map(userMapper::userToUserDTO).collect(Collectors.toList());
        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> actual = userService.findAllUsers();
        Assertions.assertIterableEquals(expected, actual);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testCreateUserWhenUserExists() {
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        when(userRepository.existsByEmail("007@gmail.com")).thenReturn(true);

        Throwable exception = assertThrows(UserExistsException.class, () ->
                userService.createUser(user)
        );
        assertEquals("This email already exist!", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail("007@gmail.com");
    }

    @Test
    void testCreateUserWhenUserNotExists() {
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        UserDTO expected = new UserDTO(1L, "James", "Bond", "007@gmail.com", null);
        when(userRepository.existsByEmail("007@gmail.com")).thenReturn(false);

        UserDTO actual = userService.createUser(user);
        Assertions.assertEquals(expected, actual);
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).existsByEmail("007@gmail.com");
    }

    @Test
    void testFindUserByIdWhenUserExists() {
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        UserDTO expected = userMapper.userToUserDTO(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO actual = userService.findUserById(1L);
        Assertions.assertEquals(expected, actual);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindUserByIdWhenUserNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(UserNotFoundException.class, () ->
                userService.findUserById(1L)
        );
        assertEquals("Not found user by id=1", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUserDataByIdWhenUserExists() {
        User updated = new User(null, "James", "James", "007@gmail.com", null);
        User user = new User(1L, "Tony", "Stark", "ironman@gmail.com", "zaq1@WSX");
        UserDTO expected = new UserDTO(1L, "James", "James", "007@gmail.com", "zaq1@WSX");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.updateUserDataById(1L, updated);
        UserDTO searchResult = userService.findUserById(1L);

        Assertions.assertEquals(expected, searchResult);
        verify(userRepository, times(2)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserDataByIdWhenUserNotExists() {
        User updated = new User(null, "James", "Bond", "007@gmail.com", null);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(UserNotFoundException.class, () ->
                userService.updateUserPassword(1L, updated)
        );
        assertEquals("Not found user by id=1", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUserPasswordWhenUserExists() {
        User updated = new User(null, null, null, null, "xsw2!QAZ");
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        UserDTO expected = new UserDTO(1L, "James", "Bond", "007@gmail.com", "xsw2!QAZ");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateUserPassword(1L, updated);
        UserDTO actual = userService.findUserById(1L);
        Assertions.assertEquals(expected, actual);
        verify(userRepository, times(2)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserPasswordWhenUserNotExists() {
        User updated = new User(null, null, null, null, "xsw2!QAZ");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(UserNotFoundException.class, () ->
                userService.updateUserPassword(1L, updated)
        );
        assertEquals("Not found user by id=1", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteUserByIdWhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUserById(1L);
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void testDeleteUserByIdWhenUserNotExists() {
        when(userRepository.existsById(1L)).thenReturn(false);

        Throwable exception = assertThrows(UserNotFoundException.class, () ->
                userService.deleteUserById(1L)
        );
        assertEquals("Not found user by id=1", exception.getMessage());
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void testFindAllLecturesByUserIdWhenUserExists() {
        List<Lecture> lectures = Arrays.asList(
                new Lecture(1L, "Java 8", "The basics of language", "Tony Stark", true),
                new Lecture(2L, "Spring", "The basics of framework", "Tony Stark", false)
        );
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        user.getLectures().addAll(lectures);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<LectureDTO> actual = userService.findAllLecturesByUserId(1L);
        List<LectureDTO> expected = lectures.stream().map(lectureMapper::lectureToLectureDTO).collect(Collectors.toList());
        Assertions.assertEquals(expected, actual);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindAllLecturesByUserIdWhenUserNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(UserNotFoundException.class, () ->
                userService.findAllLecturesByUserId(1L)
        );
        assertEquals("Not found user by id=1", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }
}