package pl.medm.javadev.utils.mapper;

import org.mapstruct.Mapper;
import pl.medm.javadev.model.dto.LectureDTO;
import pl.medm.javadev.model.entity.Lecture;

@Mapper(componentModel = "spring")
public interface LectureMapper {

    LectureDTO lectureToLectureDTO(Lecture lecture);
    Lecture lectureDTOToLecture(LectureDTO lectureDTO);
}
