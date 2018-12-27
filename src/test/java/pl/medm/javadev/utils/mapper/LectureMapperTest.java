package pl.medm.javadev.utils.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.medm.javadev.dto.LectureDTO;
import pl.medm.javadev.model.Lecture;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LectureMapperImpl.class)
public class LectureMapperTest {

    private LectureMapper lectureMapper = Mappers.getMapper(LectureMapper.class);

    @Test
    public void givenLectureToLectureDTO_whenMaps_thenCorrect() {
        Lecture lecture = new Lecture();
        lecture.setTitle("Lecture 1 - Spring basics");
        lecture.setDescription("Introduction to the Spring framework");
        lecture.setLecturer("Jan Kowalski");
        LectureDTO lectureDTO = lectureMapper.lectureToLectureDTO(lecture);

        assertEquals(lecture.getTitle(), lectureDTO.getTitle());
        assertEquals(lecture.getDescription(), lectureDTO.getDescription());
        assertEquals(lecture.getLecturer(), lectureDTO.getLecturer());
    }

    @Test
    public void givenLectureDTOToLecture_whenMaps_thenCorrect() {
        LectureDTO lectureDTO = new LectureDTO();
        lectureDTO.setTitle("Lecture 1 - Spring basics");
        lectureDTO.setDescription("Introduction to the Spring framework");
        lectureDTO.setLecturer("Jan Kowalski");
        Lecture lecture = lectureMapper.lectureDTOToLecture(lectureDTO);

        assertEquals(lectureDTO.getTitle(), lecture.getTitle());
        assertEquals(lectureDTO.getDescription(), lecture.getDescription());
        assertEquals(lectureDTO.getLecturer(), lecture.getLecturer());
    }
}
