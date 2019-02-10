package pl.medm.javadev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.medm.javadev.model.dto.LectureDTO;
import pl.medm.javadev.model.dto.UserDTO;
import pl.medm.javadev.model.entity.Lecture;
import pl.medm.javadev.repository.LectureRepository;
import pl.medm.javadev.repository.UserRepository;
import pl.medm.javadev.utils.exception.*;
import pl.medm.javadev.utils.mapper.LectureMapper;
import pl.medm.javadev.utils.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final LectureMapper lectureMapper;
    private final UserMapper userMapper;

    @Autowired
    public LectureService(LectureRepository lectureRepository, UserRepository userRepository,
                          LectureMapper lectureMapper, UserMapper userMapper) {
        this.lectureRepository = lectureRepository;
        this.lectureMapper = lectureMapper;
        this.userMapper = userMapper;
    }

    public List<LectureDTO> findAllLectures() {
        return lectureRepository.findAll().stream()
                .map(lectureMapper::lectureToLectureDTO)
                .collect(Collectors.toList());
    }

    public LectureDTO createLecture(LectureDTO dto) {
        if (lectureRepository.existsByTitle(dto.getTitle())) {
            throw new ConflictException("Conflict! Lecture title: " + dto.getTitle() + " is already busy.");
        }
        Lecture lecture = lectureMapper.lectureDTOToLecture(dto);
        lecture = lectureRepository.save(lecture);
        dto.setId(lecture.getId());
        return dto;
    }

    public LectureDTO findLectureById(Long id) {
        Optional<Lecture> searchResult = lectureRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("Lecture not found!");
        }
        return searchResult.map(lectureMapper::lectureToLectureDTO).get();
    }

    public void updateLectureById(Long id, LectureDTO dto) {
        Optional<Lecture> searchResult = lectureRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("Lecture not found!");
        }
        if (lectureRepository.existsByTitle(dto.getTitle())) {
            throw new ConflictException("Conflict! Lecture title: " + dto.getTitle() + " is already busy.");
        }
        searchResult.get().setTitle(dto.getTitle());
        searchResult.get().setDescription(dto.getDescription());
        searchResult.get().setLecturer(dto.getLecturer());
        searchResult.get().setCompleted(dto.isCompleted());
        lectureRepository.save(searchResult.get());
    }

    public void deleteLectureById(Long id) {
        Optional<Lecture> searchResult = lectureRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("Lecture not found!");
        }
        if (searchResult.get().isCompleted()) {
            throw new ForbiddenException("Forbidden!");
        }
        lectureRepository.deleteById(id);
    }

    public List<UserDTO> findAllLectureUsersById(Long id) {
        Optional<Lecture> lecture = lectureRepository.findById(id);
        if (!lecture.isPresent()) {
            throw new NotFoundException("Lecture not found!");
        }
        return lecture
                .get()
                .getUsers()
                .stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }
}