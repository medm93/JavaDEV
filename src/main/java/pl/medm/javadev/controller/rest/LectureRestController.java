package pl.medm.javadev.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.medm.javadev.model.dto.LectureDTO;
import pl.medm.javadev.model.entity.Lecture;
import pl.medm.javadev.service.LectureService;

import javax.validation.Valid;
import java.net.URI;

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
    public ResponseEntity<Object> getAllLectures() {
        return ResponseEntity.ok(lectureService.findAllLectures());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> createLecture(@Valid @RequestBody Lecture lecture, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

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
    public ResponseEntity<Object> findLectureById(@PathVariable long id) {
        return ResponseEntity.ok(lectureService.findLectureById(id));
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> updateLectureById(@PathVariable long id, @Valid @RequestBody Lecture lecture,
                                                    BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        lectureService.updateLectureById(id, lecture);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> deleteLectureById(@PathVariable Long id) {
        lectureService.deleteLectureById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{id}/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> findAllLectureUsersById(@PathVariable Long id) {
        return ResponseEntity.ok(lectureService.findAllLectureUsersById(id));
    }
}
