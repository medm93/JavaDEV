package pl.medm.javadev.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.medm.javadev.constraint.group.UserData;
import pl.medm.javadev.constraint.group.UserPassword;
import pl.medm.javadev.constraint.group.UserRegistration;
import pl.medm.javadev.model.User;
import pl.medm.javadev.service.UserService;
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
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping
    public ResponseEntity<?> saveUser(@Validated(UserRegistration.class) @RequestBody User user,
                                         BindingResult result) {
        System.out.println(user);
        return userService.createUser(user, result);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @GetMapping(path = "/{id}/lectures", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllLecturesByUserId(@PathVariable Long id) {
        return userService.findAllLecturesByUserId(id);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateUserData(@PathVariable Long id, @Validated(UserData.class) @RequestBody User user,
                                               BindingResult result) {
        return userService.updateUserData(id, user, result);
    }

    @PutMapping(path = "/{id}/password")
    public ResponseEntity<?> updateUserPassword(@PathVariable Long id, @Validated(UserPassword.class) @RequestBody User user,
                                                   BindingResult result) {
        return userService.updateUserPassword(id, user, result);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        return userService.deleteUserById(id);
    }
}
