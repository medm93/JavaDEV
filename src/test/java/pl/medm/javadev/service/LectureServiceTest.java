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
    void testWhenCreateLectureThenLectureConflict() {
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "Tony Stark", false);
        when(lectureRepository.existsByTitle("Spring Boot")).thenReturn(true);

        Throwable exception = assertThrows(ConflictException.class, () ->
                lectureService.createLecture(lecture)
        );

        Assertions.assertEquals("Lecture conflict!", exception.getMessage());
        verify(lectureRepository, times(1)).existsByTitle("Spring Boot");
        verify(lectureRepository, times(0)).save(lecture);
    }

    @Test
    void testWhenCreateLectureThenLectureNotConflict() {
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

        Throwable exception = assertThrows(NotFoundException.class, () ->
                lectureService.findLectureById(1L)
        );

        Assertions.assertEquals("Lecture not found!", exception.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
    }

    //UPDATE LECTURE BY ID
    @Test
    void testWhenUpdateLectureByIdThenLectureFoundAndTitleConflict() {
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "Tony Stark", false);
        Lecture updated = new Lecture("Java 8", "The basic of language", "James Bond", true);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        when(lectureRepository.existsByTitle("Java 8")).thenReturn(true);

        Throwable exception = assertThrows(ConflictException.class, () ->
                lectureService.updateLectureById(1L, updated)
        );

        Assertions.assertEquals("Lecture conflict!", exception.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
        verify(lectureRepository, times(1)).existsByTitle("Java 8");
        verify(lectureRepository, times(0)).save(lecture);

    }

    @Test
    void testWhenUpdateLectureByIdThenLectureFoundAndTitleNotConflict() {
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

        Throwable exception = assertThrows(NotFoundException.class, () ->
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

        Throwable exception = assertThrows(ForbiddenException.class, () ->
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

        Throwable exception = assertThrows(NotFoundException.class, () ->
                lectureService.deleteLectureById(1L)
        );
        Assertions.assertEquals("Lecture not found!", exception.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
        verify(lectureRepository, times(0)).deleteById(1L);
    }

    //FIND ALL LECTURER USERS
    @Test
    void testWhenFindAllLectureUsersThenLectureNotFound() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class, () ->
                lectureService.findAllLectureUsersById(1L)
        );

        Assertions.assertEquals("Lecture not found!", exception.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
    }

    @Test
    void testWhenFindAllLectureUsersByIdThenLectureFound() {
        List<User> users = Arrays.asList(
                new User(1L, "Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                        "1", "Automatics", "000001"),
                new User(2L, "Steven", "Rogers", "capitan.america@marvel.com", "zaq1@WSX",
                        "2", "Electronics", "000002")
        );
        Lecture lecture = new Lecture(1L, "Spring Boot", "The basics of framework", "James Bond", false);
        lecture.getUsers().addAll(users);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        List<UserDTO> expected = users.stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
        List<UserDTO> actual = lectureService.findAllLectureUsersById(1L);
        Assertions.assertEquals(expected, actual);
        verify(lectureRepository, times(1)).findById(1L);
    }
}
