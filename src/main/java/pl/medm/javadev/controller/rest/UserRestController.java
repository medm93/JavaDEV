package pl.medm.javadev.controller.rest;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.medm.javadev.constraint.group.PasswordData;
import pl.medm.javadev.constraint.group.CreateUser;
import pl.medm.javadev.constraint.group.UpdateUser;
import pl.medm.javadev.model.dto.LectureDTO;
import pl.medm.javadev.model.dto.UserDTO;
import pl.medm.javadev.model.entity.User;
import pl.medm.javadev.service.UserService;
import pl.medm.javadev.utils.exception.ConflictException;
import pl.medm.javadev.utils.exception.NotFoundException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Log4j2
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> findAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        log.info("Received {} results for users search", users.size());
        return ResponseEntity.ok(users);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> createUser(@Validated(CreateUser.class) @RequestBody UserDTO dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.warn("User validate failed");
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }
            dto = userService.createUser(dto);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(dto.getId())
                    .toUri();
            log.info("User [id={}] created", dto.getId());
            return ResponseEntity.created(location).body(dto);
        } catch (ConflictException e) {
            log.error("Conflict! Email {} or index number {} is already busy.", dto.getEmail(), dto.getIndexNumber(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Object> findUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.findUserById(id);
            log.info("User [id={} found", id);
            return ResponseEntity.ok(user);
        } catch (NotFoundException e) {
            log.error("User [id={}] not found", id, e);
            return ResponseEntity.notFound().build();
        }



    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Object> updateUserById(@PathVariable Long id, @Validated(UpdateUser.class) @RequestBody UserDTO dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.warn("User validate failed");
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }
            userService.updateUserById(id, dto);
            log.info("User [id={}] updated", id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            log.error("User [id={}] not found", id, e);
            return ResponseEntity.notFound().build();
        } catch (ConflictException e) {
            log.error("Conflict! Email {} or index number {} is already busy.", dto.getEmail(), dto.getIndexNumber(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping(path = "/{id}/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Object> updateUserPasswordById(@PathVariable Long id, @Validated(PasswordData.class) @RequestBody UserDTO dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.warn("User password validate failed");
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }
            userService.updateUserPasswordById(id, dto);
            log.info("User password [id={}] updated", id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            log.error("User [id={}] not found", id, e);
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            log.info("User password [id={}] updated", id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            log.error("User [id={}] not found", id, e);
            return ResponseEntity.notFound().build();
        }


    }

    @GetMapping(path = "/{id}/lectures")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Object> findAllLecturesByUserId(@PathVariable Long id) {
        try {
            List<LectureDTO> lectures = userService.findAllUserLecturesById(id);
            log.info("Received {} results for user lectures search", lectures.size());
            return ResponseEntity.ok(lectures);
        } catch (NotFoundException e) {
            log.error("User [id={}] not found", id, e);
            return ResponseEntity.notFound().build();
        }


    }
}
