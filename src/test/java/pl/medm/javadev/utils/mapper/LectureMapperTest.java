package pl.medm.javadev.utils.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import pl.medm.javadev.model.dto.LectureDTO;
import pl.medm.javadev.model.entity.Lecture;

@SpringBootTest(classes = LectureMapperImpl.class)
class LectureMapperTest {

    private LectureMapper lectureMapper = Mappers.getMapper(LectureMapper.class);

    @Test
    void testWhenLectureToLectureDTOThenCorrect() {
        Lecture lecture = new Lecture(1L, "Java 8", "The basics of language", "Howard Stark", true);

        LectureDTO lectureDTO = lectureMapper.lectureToLectureDTO(lecture);

        Assertions.assertEquals(lecture.getId(), lectureDTO.getId());
        Assertions.assertEquals(lecture.getTitle(), lectureDTO.getTitle());
        Assertions.assertEquals(lecture.getDescription(), lectureDTO.getDescription());
        Assertions.assertEquals(lecture.getLecturer(), lectureDTO.getLecturer());
        Assertions.assertEquals(lecture.isCompleted(), lectureDTO.isCompleted());
    }

    @Test
    void testWhenLectureDTOToLectureThenCorrect() {
        LectureDTO lectureDTO = new LectureDTO(1L, "Java 8", "The basics of language", "Howard Stark", true);

        Lecture lecture = lectureMapper.lectureDTOToLecture(lectureDTO);

        Assertions.assertEquals(lectureDTO.getId(), lecture.getId());
        Assertions.assertEquals(lectureDTO.getTitle(), lecture.getTitle());
        Assertions.assertEquals(lectureDTO.getDescription(), lecture.getDescription());
        Assertions.assertEquals(lectureDTO.getLecturer(), lecture.getLecturer());
        Assertions.assertEquals(lectureDTO.isCompleted(), lecture.isCompleted());
    }
}
