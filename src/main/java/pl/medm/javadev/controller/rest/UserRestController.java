package pl.medm.javadev.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.medm.javadev.constraint.group.UserData;
import pl.medm.javadev.constraint.group.UserPassword;
import pl.medm.javadev.constraint.group.UserRegistration;
import pl.medm.javadev.dto.LectureDTO;
import pl.medm.javadev.dto.UserDTO;
import pl.medm.javadev.model.User;
import pl.medm.javadev.service.UserService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> findAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> saveUser(@Validated(UserRegistration.class) @RequestBody User user,
                                           BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        UserDTO userDTO = userService.createUser(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userDTO.getId())
                .toUri();
        return ResponseEntity.created(location).body(userDTO);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDTO> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping(path = "/{id}/lectures")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<LectureDTO>> findAllLecturesByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findAllLecturesByUserId(id));
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Object> updateUserDataById(@PathVariable Long id, @Validated(UserData.class) @RequestBody User user,
                                                     BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        userService.updateUserDataById(id, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Object> updateUserPasswordById(@PathVariable Long id, @Validated(UserPassword.class) @RequestBody User user,
                                                         BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        userService.updateUserPassword(id, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
