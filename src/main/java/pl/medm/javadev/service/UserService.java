package pl.medm.javadev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.medm.javadev.model.Lecture;
import pl.medm.javadev.model.User;
import pl.medm.javadev.repository.UserRepository;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public ResponseEntity<?> createUser(User user, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Long id = userRepository.save(user).getId();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).body(user);
    }

    public ResponseEntity<?> findUserById(Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> updateUserData(Long id, User user, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User userInDB = searchResult.get();
        userInDB.setFirstName(user.getFirstName());
        userInDB.setLastName(user.getLastName());
        userInDB.setEmail(user.getEmail());
        userInDB.setYearOfStudy(user.getYearOfStudy());
        userInDB.setFieldOfStudy(user.getFieldOfStudy());
        userInDB.setIndexNumber(user.getIndexNumber());
        userRepository.save(userInDB);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> updateUserPassword(Long id, User user, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User userInDB = searchResult.get();
        userInDB.setPassword(user.getPassword());
        userRepository.save(userInDB);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> findAllLecturesByUserId(Long id) {
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<Lecture> lectures = searchResult.get().getLectures();
        return ResponseEntity.ok(lectures);
    }
}