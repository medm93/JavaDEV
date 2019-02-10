package pl.medm.javadev.controller.rest;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.medm.javadev.model.dto.LectureDTO;
import pl.medm.javadev.model.dto.UserDTO;
import pl.medm.javadev.model.entity.Lecture;
import pl.medm.javadev.service.LectureService;
import pl.medm.javadev.utils.exception.ConflictException;
import pl.medm.javadev.utils.exception.NotFoundException;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@Log4j2
@RequestMapping("/api/lectures")
public class LectureRestController {

    private LectureService lectureService;

    @Autowired
    public LectureRestController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Object> getAllLectures() {
        List<LectureDTO> lectures = lectureService.findAllLectures();
        log.info("Received {} results for search.", lectures.size());
        return ResponseEntity.ok(lectures);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> createLecture(@Valid @RequestBody LectureDTO dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.warn("Lecture validate failed.");
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }
            dto = lectureService.createLecture(dto);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(dto.getId())
                    .toUri();
            log.info("Lecture [id={}] created.", dto.getId());
            return ResponseEntity.created(location).body(dto);
        } catch (ConflictException e) {
            log.error("Conflict! Lecture title: [{}] is already busy.", dto.getTitle(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Object> findLectureById(@PathVariable Long id) {
        try {
            LectureDTO lecture = lectureService.findLectureById(id);
            log.info("Lecture [id={}] found.", id);
            return ResponseEntity.ok(lecture);
        } catch (NotFoundException e) {
            log.error("Lecture [id={}] not found.", id, e);
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> updateLectureById(@PathVariable Long id, @Valid @RequestBody LectureDTO dto,
                                                    BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.warn("Lecture validate failed.");
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }
            lectureService.updateLectureById(id, dto);
            log.info("Lecture [id={}] updated", id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            log.error("Lecture [id={}] not found.", id, e);
            return ResponseEntity.notFound().build();
        } catch (ConflictException e) {
            log.error("Conflict! Lecture title: [{}] is already busy.", dto.getTitle(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> deleteLectureById(@PathVariable Long id) {
        try {
            lectureService.deleteLectureById(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            log.error("Lecture [id={}] not found.", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/{id}/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> findAllLectureUsersById(@PathVariable Long id) {
        try {
            List<UserDTO> users = lectureService.findAllLectureUsersById(id);
            log.info("Received {} results for lecture users search.", users.size());
            return ResponseEntity.ok(users);
        } catch (NotFoundException e) {
            log.error("Lecture [id={}] not found.", id, e);
            return ResponseEntity.notFound().build();
        }
    }
}
