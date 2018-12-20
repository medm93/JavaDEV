package pl.medm.javadev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.medm.javadev.dto.LectureDTO;
import pl.medm.javadev.dto.UserDTO;
import pl.medm.javadev.model.Lecture;
import pl.medm.javadev.model.User;
import pl.medm.javadev.repository.LectureRepository;
import pl.medm.javadev.repository.UserRepository;
import pl.medm.javadev.utils.exception.LectureExistsException;
import pl.medm.javadev.utils.exception.LectureIsEndException;
import pl.medm.javadev.utils.exception.LectureNotFoundException;
import pl.medm.javadev.utils.exception.UserExistsException;
import pl.medm.javadev.utils.mapper.LectureMapper;
import pl.medm.javadev.utils.mapper.UserMapper;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LectureService {

    private LectureRepository lectureRepository;
    private UserRepository userRepository;
    private LectureMapper lectureMapper;
    private UserMapper userMapper;

    @Autowired
    public LectureService(LectureRepository lectureRepository, UserRepository userRepository,
                          LectureMapper lectureMapper, UserMapper userMapper) {
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
        this.lectureMapper = lectureMapper;
        this.userMapper = userMapper;
    }

    public Stream<LectureDTO> findAllLectures() {
        return lectureRepository.findAll()
                .stream()
                .map(lectureMapper::lectureToLectureDTO);
    }

    public LectureDTO createLecture(Lecture lecture) {
        if (lectureRepository.existsByTitle(lecture.getTitle())) {
            throw new LectureExistsException("Lecture with this title exists");
        }
        lectureRepository.save(lecture);
        return lectureMapper.lectureToLectureDTO(lecture);
//        Long id = lectureRepository.save(lecture).getId();
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(id)
//                .toUri();
//        return ResponseEntity.created(location).body(lecture);
    }

    public LectureDTO findLectureById(Long id) {
        Optional<Lecture> searchResult = lectureRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new LectureNotFoundException("Not found lecture by id=" + id);
        }
        return searchResult.map(lectureMapper::lectureToLectureDTO).get();
    }

    public void updateLectureById(Long id, Lecture lecture) {
        Optional<Lecture> searchResult = lectureRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new LectureNotFoundException("Not found lecture by id=" + id);
        }
        Lecture lectureInDB = searchResult.get();
        lectureInDB.setTitle(lecture.getTitle());
        lectureInDB.setDescription(lecture.getDescription());
        lectureInDB.setLecturer(lecture.getLecturer());
        lectureRepository.save(lectureInDB);
    }

    public void deleteLectureById(Long id) {
        Optional<Lecture> searchResult = lectureRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new LectureNotFoundException("Not found lecture by id=" + id);
        }
        if (searchResult.get().getCompleted()) {
            throw new LectureIsEndException("This lecture is completed");
        }
        lectureRepository.deleteById(id);
    }

    public List<UserDTO> getAllUserById(Long id) {
        Optional<Lecture> lecture = lectureRepository.findById(id);
        if (!lecture.isPresent()) {
            throw new LectureNotFoundException("Not found lecture by id=" + id);
        }
        return lecture
                .get()
                .getUsers()
                .stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO saveUserToLecture(Long id, User user) {
        Optional<Lecture> lectureSearchResult = lectureRepository.findById(id);
        Optional<User> userSearchResult = userRepository.findById(user.getId());
        if (!lectureSearchResult.isPresent()) {
            throw new LectureNotFoundException("Not found lecture by id=" + id);
        }
        if (!userSearchResult.isPresent()) {
            throw new UserExistsException("This lecture has already this user");
        }
        Lecture lecture = lectureSearchResult.get();
        User userInDB = userSearchResult.get();
        lecture.addUser(userInDB);
        lectureRepository.save(lecture);
        return userMapper.userToUserDTO(userInDB);
    }
}