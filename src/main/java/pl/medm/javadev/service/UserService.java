package pl.medm.javadev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.medm.javadev.model.dto.LectureDTO;
import pl.medm.javadev.model.dto.UserDTO;
import pl.medm.javadev.model.dto.UserPasswordDTO;
import pl.medm.javadev.model.entity.Role;
import pl.medm.javadev.model.entity.User;
import pl.medm.javadev.repository.RoleRepository;
import pl.medm.javadev.repository.UserRepository;
import pl.medm.javadev.utils.exception.ConflictException;
import pl.medm.javadev.utils.exception.NotFoundException;
import pl.medm.javadev.utils.mapper.LectureMapper;
import pl.medm.javadev.utils.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final String DEFAULT_ROLE = "ROLE_USER";
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;
    private LectureMapper lectureMapper;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                       UserMapper userMapper, LectureMapper lectureMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.lectureMapper = lectureMapper;
    }

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO createUser(User user) {
        if (userRepository.existsByEmailOrIndexNumber(user.getEmail(), user.getIndexNumber())) {
            throw new ConflictException("User conflict!");
        }
        return addWithDefaultRole(user);
    }

    public UserDTO findUserById(long id) {
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("User not found!");
        }
        return searchResult.map(userMapper::userToUserDTO).get();
    }

    public UserPasswordDTO findUserPasswordById(long id) {
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("User not found!");
        }
        return searchResult.map(userMapper::userToUserPasswordDTO).get();
    }

    public void updateUserById(long id, User user) {
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("User not found!");
        }
        if (userRepository.existsByEmailOrIndexNumber(user.getEmail(), user.getIndexNumber())) {
            throw new ConflictException("User conflict!");
        }
        searchResult.get().setFirstName(user.getFirstName());
        searchResult.get().setLastName(user.getLastName());
        searchResult.get().setEmail(user.getEmail());
        searchResult.get().setYearOfStudy(user.getYearOfStudy());
        searchResult.get().setFieldOfStudy(user.getFieldOfStudy());
        searchResult.get().setIndexNumber(user.getIndexNumber());
        userRepository.save(searchResult.get());
    }

    public void updateUserPasswordById(long id, User user) {
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("User not found!");
        }
        searchResult.get().setPassword(user.getPassword());
        userRepository.save(searchResult.get());
    }

    public void deleteUserById(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found!");
        }
        userRepository.deleteById(id);
    }

    public List<LectureDTO> findAllUserLecturesById(long id) {
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("User not found!");
        }
        return searchResult
                .get()
                .getLectures()
                .stream()
                .map(lectureMapper::lectureToLectureDTO)
                .collect(Collectors.toList());
    }

    private UserDTO addWithDefaultRole(User user) {
        Role defaultRole = roleRepository.findByRole(DEFAULT_ROLE);
        user.getRoles().add(defaultRole);
        String passwordHash = passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordHash);
        userRepository.save(user);
        return userMapper.userToUserDTO(user);
    }
}