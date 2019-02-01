package pl.medm.javadev.utils.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.medm.javadev.model.dto.LectureDTO;
import pl.medm.javadev.model.entity.Lecture;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LectureMapperImpl.class)
public class LectureMapperTest {

    private LectureMapper lectureMapper = Mappers.getMapper(LectureMapper.class);

    @Test
    public void testWhenLectureToLectureDTOThenCorrect() {
        Lecture lecture = new Lecture();
        lecture.setTitle("Lecture 1 - Spring basics");
        lecture.setDescription("Introduction to the Spring framework");
        lecture.setLecturer("Jan Kowalski");
        lecture.setCompleted(false);
        LectureDTO lectureDTO = lectureMapper.lectureToLectureDTO(lecture);

        assertEquals(lecture.getTitle(), lectureDTO.getTitle());
        assertEquals(lecture.getDescription(), lectureDTO.getDescription());
        assertEquals(lecture.getLecturer(), lectureDTO.getLecturer());
        assertEquals(lecture.isCompleted(), lectureDTO.isCompleted());
    }

    @Test
    public void testWhenLectureDTOToLectureThenCorrect() {
        LectureDTO lectureDTO = new LectureDTO();
        lectureDTO.setTitle("Lecture 1 - Spring basics");
        lectureDTO.setDescription("Introduction to the Spring framework");
        lectureDTO.setLecturer("Jan Kowalski");
        lectureDTO.setCompleted(false);
        Lecture lecture = lectureMapper.lectureDTOToLecture(lectureDTO);

        assertEquals(lectureDTO.getTitle(), lecture.getTitle());
        assertEquals(lectureDTO.getDescription(), lecture.getDescription());
        assertEquals(lectureDTO.getLecturer(), lecture.getLecturer());
        assertEquals(lectureDTO.isCompleted(), lecture.isCompleted());
    }
}
