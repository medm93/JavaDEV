package pl.medm.javadev.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.medm.javadev.model.User;
import pl.medm.javadev.repository.UserRepository;
import pl.medm.javadev.service.UserService;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserControllerRest {

    private UserRepository userRepository;
    private List<User> users;
    @Autowired
    private UserService userService;


    @Autowired
    public UserControllerRest(UserRepository userRepository) {
        this.userRepository = userRepository;
        users = Collections.synchronizedList(new ArrayList<User>());
    }

    @GetMapping
    public List<User> findAll() {
        return userRepository.findAll();
    }

    //CREATE
    @PostMapping
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    //READ
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    //UPDATE

    //DELETE
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

}
