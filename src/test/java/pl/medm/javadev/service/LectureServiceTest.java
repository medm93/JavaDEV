package pl.medm.javadev.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.medm.javadev.model.dto.LectureDTO;
import pl.medm.javadev.model.dto.UserDTO;
import pl.medm.javadev.model.entity.Lecture;
import pl.medm.javadev.model.entity.User;
import pl.medm.javadev.repository.LectureRepository;
import pl.medm.javadev.repository.UserRepository;
import pl.medm.javadev.utils.exception.*;
import pl.medm.javadev.utils.mapper.LectureMapper;
import pl.medm.javadev.utils.mapper.UserMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectureServiceTest {

    @Mock
    private LectureRepository lectureRepository;
    @Mock
    private UserRepository userRepository;

    private LectureMapper lectureMapper = Mappers.getMapper(LectureMapper.class);

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private LectureService lectureService;

    @BeforeEach
    void setup() {
        this.lectureService = new LectureService(lectureRepository, userRepository, lectureMapper, userMapper);
    }

    //FIND ALL LECTURES
    @Test
    void testWhenFindAllLectures() {
        List<Lecture> lectures = Arrays.asList(
                new Lecture(1L, "Java 8", "The basics of language", "Tony Stark", true),
                new Lecture(2L, "Spring Boot", "The basics of framework", "Tony Stark", false)
        );
        when(lectureRepository.findAll()).thenReturn(lectures);

        List<LectureDTO> expected = lectures.stream()
                .map(lectureMapper::lectureToLectureDTO)
                .collect(Collectors.toList());
        List<LectureDTO> actual = lectureService.findAllLectures();

        Assertions.assertIterableEquals(expected, actual);
        verify(lectureRepository, times(1)).findAll();
    }

    //CREATE LECTURES
    @Test
    void testWhenCreateLectureThenLectureExists() {
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "Tony Stark", false);
        when(lectureRepository.existsByTitle("Spring Boot")).thenReturn(true);

        Throwable exception = assertThrows(LectureExistsException.class, () ->
                lectureService.createLecture(lecture)
        );

        Assertions.assertEquals("Lecture exists!", exception.getMessage());
        verify(lectureRepository, times(1)).existsByTitle("Spring Boot");
        verify(lectureRepository, times(0)).save(lecture);
    }

    @Test
    void testWhenCreateLectureThenLectureNotExists() {
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "Tony Stark", false);
        when(lectureRepository.existsByTitle("Spring Boot")).thenReturn(false);

        LectureDTO expected = lectureMapper.lectureToLectureDTO(lecture);
        LectureDTO actual = lectureService.createLecture(lecture);

        Assertions.assertEquals(expected, actual);
        verify(lectureRepository, times(1)).existsByTitle("Spring Boot");
        verify(lectureRepository, times(1)).save(lecture);
    }

    //FIND LECTURE BY ID
    @Test
    void testWhenFindLectureByIdThenLectureFound() {
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "Tony Stark", false);
        when(lectureRepository.findById(1L)).thenReturn(java.util.Optional.of(lecture));

        LectureDTO expected = lectureMapper.lectureToLectureDTO(lecture);
        LectureDTO actual = lectureService.findLectureById(1L);

        Assertions.assertEquals(expected, actual);
        verify(lectureRepository, times(1)).findById(1L);
    }

    @Test
    void testWhenFindLectureByIdThenLectureNotFound() {
        when(lectureRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        Throwable exception = assertThrows(LectureNotFoundException.class, () ->
                lectureService.findLectureById(1L)
        );

        Assertions.assertEquals("Lecture not found!", exception.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
    }

    //UPDATE LECTURE BY ID
    @Test
    void testWhenUpdateLectureByIdThenLectureFoundAndTitleExists() {
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "Tony Stark", false);
        Lecture updated = new Lecture("Java 8", "The basic of language", "James Bond", true);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        when(lectureRepository.existsByTitle("Java 8")).thenReturn(true);

        Throwable exception = assertThrows(LectureConflictException.class, () ->
                lectureService.updateLectureById(1L, updated)
        );

        Assertions.assertEquals("Title exists!", exception.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
        verify(lectureRepository, times(1)).existsByTitle("Java 8");
        verify(lectureRepository, times(0)).save(lecture);

    }

    @Test
    void testWhenUpdateLectureByIdThenLectureFoundAndTitleNotExists() {
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "Tony Stark", false);
        Lecture updated = new Lecture("Java 8", "The basic of language", "James Bond", true);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        when(lectureRepository.existsByTitle("Java 8")).thenReturn(false);

        lectureService.updateLectureById(1L, updated);
        LectureDTO expected = new LectureDTO(1L, "Java 8", "The basic of language", "James Bond", true);
        LectureDTO actual = lectureService.findLectureById(1L);

        Assertions.assertEquals(expected, actual);
        verify(lectureRepository, times(2)).findById(1L);
        verify(lectureRepository, times(1)).existsByTitle("Java 8");
        verify(lectureRepository, times(1)).save(lecture);
    }

    @Test
    void testWhenUpdateLectureByIdThenLectureNotFound() {
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "Tony Stark", false);
        Lecture updated = new Lecture("Java 8", "The basic of language", "James Bond", true);
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(LectureNotFoundException.class, () ->
                lectureService.updateLectureById(1L, updated)
        );

        Assertions.assertEquals("Lecture not found!", exception.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
        verify(lectureRepository, times(0)).existsByTitle("Java 8");
        verify(lectureRepository, times(0)).save(lecture);
    }

    //DELETE BY ID
    @Test
    void testWhenDeleteByIdThenLectureFoundAndCompleted() {
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "Tony Stark", true);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        Throwable exception = assertThrows(LectureForbiddenException.class, () ->
                lectureService.deleteLectureById(1L)
        );

        Assertions.assertEquals("Forbidden!", exception.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
        verify(lectureRepository, times(0)).save(lecture);
    }

    @Test
    void testWhenDeleteByIdThenLectureFoundAndNotCompleted() {
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "Tony Stark", false);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        lectureService.deleteLectureById(1L);

        verify(lectureRepository, times(1)).findById(1L);
        verify(lectureRepository, times(1)).deleteById(1L);
    }

    @Test
    void testWhenDeleteByIdThenLectureNotFound() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(LectureNotFoundException.class, () ->
                lectureService.deleteLectureById(1L)
        );
        Assertions.assertEquals("Lecture not found!", exception.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
        verify(lectureRepository, times(0)).deleteById(1L);
    }


    @Test
    void testDeleteLectureByIdWhenLectureNotExists() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(LectureNotFoundException.class, () ->
                lectureService.findLectureById(1L)
        );
        Assertions.assertEquals("Not found lecture by id=1", exception.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
    }

    @Test
    void testFindAllUsersByLectureIdWhenLectureExists() {
        List<User> users = Arrays.asList(
                new User(1L, "Tony", "Stark", "ironman@gmail.com", "zaq1@WSX"),
                new User(2L, "James", "Bond", "007@gmail.com", "zaq1@WSX")
        );
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "Tony Stark", false);
        lecture.getUsers().addAll(users);
        List<UserDTO> expected = users.stream().map(userMapper::userToUserDTO).collect(Collectors.toList());
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        List<UserDTO> actual = lectureService.findAllUsersForLectureById(1L);
        Assertions.assertEquals(expected, actual);
        verify(lectureRepository, times(1)).findById(1L);
    }

    @Test
    void testFindAllUsersByLectureIdWhenLectureNotExists() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(LectureNotFoundException.class, () ->
                lectureService.findLectureById(1L)
        );
        Assertions.assertEquals("Not found lecture by id=1", exception.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
    }

    @Test
    void testAddUserToLectureWhenUserExistsAndLectureExists() {
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "Tony Stark", false);
        UserDTO expected = userMapper.userToUserDTO(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        UserDTO actual = lectureService.saveUserToLecture(1L, user);
        Assertions.assertEquals(expected, actual);
        verify(userRepository, times(1)).findById(1L);
        verify(lectureRepository, times(1)).findById(1L);
        verify(lectureRepository, times(1)).save(lecture);
    }

    @Test
    void testAddUserToLectureWhenUserNotExistsAndLectureExists() {
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "Tony Stark", false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        Throwable exception = assertThrows(UserExistsException.class, () ->
                lectureService.saveUserToLecture(1L, user)
        );
        Assertions.assertEquals("This lecture has already this user", exception.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
        verify(lectureRepository, times(1)).findById(1L);
    }

    @Test
    void testAddUserToLectureWhenUserExistsAndLectureNotExists() {
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(LectureNotFoundException.class, () ->
                lectureService.saveUserToLecture(1L, user)
        );
        Assertions.assertEquals("Not found lecture by id=1", exception.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
        verify(lectureRepository, times(1)).findById(1L);
    }

    @Test
    void testAddUserToLectureWhenUserNotExistsAndLectureNotExists() {
        User user = new User(1L, "James", "Bond", "007@gmail.com", "zaq1@WSX");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(LectureNotFoundException.class, () ->
                lectureService.saveUserToLecture(1L, user)
        );
        Assertions.assertEquals("Not found lecture by id=1", (exception.getMessage()));
        verify(lectureRepository, times(1)).findById(1L);
        verify(lectureRepository, times(1)).findById(1L);
    }
}
