package pl.medm.javadev.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.medm.javadev.model.Lecture;
import pl.medm.javadev.model.User;
import pl.medm.javadev.service.LectureService;

import java.util.List;

@RestController
@RequestMapping("/lectures")
public class LectureRestController {

    private LectureService lectureService;

    @Autowired
    public LectureRestController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    //U wyświetl wszystkie wykłady
    @GetMapping
    public List<Lecture> getAllLectures() {
        return lectureService.getAllLectures();
    }

    @PostMapping
    public ResponseEntity<?> createLecture(@RequestBody Lecture lecture) {
        return lectureService.createLecture(lecture);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readLecture(@PathVariable Long id) {
        return lectureService.findLectureById(id);
    }

    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Lecture> updateLecture(@PathVariable Long id, @RequestBody Lecture lecture) {
        return lectureService.updateLectureById(id, lecture);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteLecture(@PathVariable Long id) {
        return lectureService.deleteLectureById(id);
    }

    @GetMapping(path = "/{id}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getAttendance(@PathVariable Long id) {
        return lectureService.getAllUserById(id);
    }

    //wpisanie się studenta na listę obecności na konkretnych zajęciach
    @PostMapping(path = "/{id}/users")
    public ResponseEntity<?> saveUserToLecture(@PathVariable Long id, @RequestBody User user) {
        return lectureService.saveUserToLecture(id, user);
    }
}
