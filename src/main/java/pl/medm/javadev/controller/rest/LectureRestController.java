package pl.medm.javadev.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.medm.javadev.model.Lecture;
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

    @GetMapping
    public List<Lecture> findAll() {
        return lectureService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> saveLecture(@RequestBody Lecture lecture) {
        return lectureService.saveLecture(lecture);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLecture(@PathVariable Long id) {
        return lectureService.getLecture(id);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return lectureService.deleteUser(id);
    }
}
