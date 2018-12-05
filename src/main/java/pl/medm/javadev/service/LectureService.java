package pl.medm.javadev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.medm.javadev.model.Lecture;
import pl.medm.javadev.model.User;
import pl.medm.javadev.repository.LectureRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class LectureService {

    private LectureRepository lectureRepository;

    @Autowired
    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    public List<Lecture> getAllLectures() {
        return lectureRepository.findAll();
    }

    public ResponseEntity<?> createLecture(Lecture lecture) {
        if (lectureRepository.existsByTitle(lecture.getTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            Long id = lectureRepository.save(lecture).getId();
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(id)
                    .toUri();
            return ResponseEntity.created(location).body(lecture);
        }
    }

    public ResponseEntity<?> findLectureById(Long id) {
        return lectureRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<Lecture> updateLectureById(Long id, Lecture newLecture) {
        if (lectureRepository.existsById(id)) {
            Lecture lecture = lectureRepository.findById(id).get();
            lecture.setTitle(newLecture.getTitle());
            lecture.setDescription(newLecture.getDescription());
            lecture.setLecturer(newLecture.getLecturer());
            lectureRepository.save(lecture);
            return ResponseEntity.ok(lecture);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<?> deleteLectureById(Long id) {
        if (lectureRepository.existsById(id)) {
            if (lectureRepository.findById(id).get().getCompleted()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            lectureRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<List<User>> getAllUserById(Long id) {
        Optional<Lecture> lecture = lectureRepository.findById(id);
        if (lecture.isPresent()) {
            List<User> users = lecture.get().getUsers();
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<User> saveUser(Long id, User user) {
        if (lectureRepository.existsById(id)) {
            Lecture lecture = lectureRepository.findById(id).get();
            lecture.getUsers().add(user);
            lectureRepository.save(lecture);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(user.getId())
                    .toUri();
            return ResponseEntity.created(location).body(user);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


}
