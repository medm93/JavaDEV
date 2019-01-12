package pl.medm.javadev.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.medm.javadev.model.dto.LectureDTO;
import pl.medm.javadev.model.dto.UserDTO;
import pl.medm.javadev.model.entity.Lecture;
import pl.medm.javadev.model.entity.User;
import pl.medm.javadev.service.LectureService;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/lectures")
public class LectureRestController {

    private LectureService lectureService;

    @Autowired
    public LectureRestController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Stream<LectureDTO>> getAllLectures() {
        return ResponseEntity.ok(lectureService.findAllLectures());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> createLecture(@RequestBody Lecture lecture) {
        LectureDTO lectureDTO = lectureService.createLecture(lecture);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(lectureDTO.getId())
                .toUri();
        return ResponseEntity.created(location).body(lectureDTO);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<LectureDTO> findLectureById(@PathVariable Long id) {
        return ResponseEntity.ok(lectureService.findLectureById(id));
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> updateLecture(@PathVariable Long id, @RequestBody Lecture lecture) {
        lectureService.updateLectureById(id, lecture);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> deleteLecture(@PathVariable Long id) {
        lectureService.deleteLectureById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{id}/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<UserDTO>> getAttendance(@PathVariable Long id) {
        return ResponseEntity.ok(lectureService.getAllUserById(id));
    }

    @PostMapping(path = "/{id}/users")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<UserDTO> saveUserToLecture(@PathVariable Long id, @RequestBody User user) {
        UserDTO userDTO = lectureService.saveUserToLecture(id, user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();
        return ResponseEntity.created(location).body(userDTO);
    }
}
