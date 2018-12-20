package pl.medm.javadev.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> findAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PostMapping
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

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping(path = "/{id}/lectures", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LectureDTO>> findAllLecturesByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findAllLecturesByUserId(id));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updateUserData(@PathVariable Long id, @Validated(UserData.class) @RequestBody User user,
                                                 BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        userService.updateUserDataById(id, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}/password")
    public ResponseEntity<Object> updateUserPassword(@PathVariable Long id, @Validated(UserPassword.class) @RequestBody User user,
                                                     BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        userService.updateUserPassword(id, user);
        return ResponseEntity.noContent().build();
    }

//    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
