package pl.medm.javadev.utils.mapper;

import org.mapstruct.Mapper;
import pl.medm.javadev.dto.LectureDTO;
import pl.medm.javadev.model.Lecture;

@Mapper(componentModel = "spring")
public interface LectureMapper {

    LectureDTO lectureToLectureDTO(Lecture lecture);
    Lecture lectureDTOToLecture(LectureDTO lectureDTO);
}
