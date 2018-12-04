package pl.medm.javadev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.medm.javadev.model.Lecture;
import pl.medm.javadev.repository.LectureRepository;

import java.net.URI;
import java.util.List;

@Service
public class LectureService {

    private LectureRepository lectureRepository;

    @Autowired
    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    public List<Lecture> findAll() {
        return lectureRepository.findAll();
    }

    public ResponseEntity<?> saveLecture(Lecture lecture) {
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

    public ResponseEntity<?> getLecture(Long id) {
        return lectureRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> deleteUser(Long id) {
        if(lectureRepository.existsById(id)) {
            lectureRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
