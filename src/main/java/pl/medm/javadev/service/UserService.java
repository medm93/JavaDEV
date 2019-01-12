package pl.medm.javadev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.medm.javadev.model.dto.LectureDTO;
import pl.medm.javadev.model.dto.UserDTO;
import pl.medm.javadev.model.entity.Role;
import pl.medm.javadev.model.entity.User;
import pl.medm.javadev.repository.RoleRepository;
import pl.medm.javadev.repository.UserRepository;
import pl.medm.javadev.utils.exception.UserExistsException;
import pl.medm.javadev.utils.exception.UserNotFoundException;
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
        return userRepository.findAll()
                .stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserExistsException("This email already exist!");
        }
        return addWithDefaultRole(user);
    }

    public UserDTO findUserById(Long id) {
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new UserNotFoundException("Not found user by id=" + id);
        }
        return searchResult.map(userMapper::userToUserDTO).get();
    }

    public void updateUserDataById(Long id, User user) {
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new UserNotFoundException("Not found user by id=" + id);
        }
        User userInDB = searchResult.get();
        userInDB.setFirstName(user.getFirstName());
        userInDB.setLastName(user.getLastName());
        userInDB.setEmail(user.getEmail());
        userInDB.setYearOfStudy(user.getYearOfStudy());
        userInDB.setFieldOfStudy(user.getFieldOfStudy());
        userInDB.setIndexNumber(user.getIndexNumber());
        userRepository.save(userInDB);
    }

    public void updateUserPassword(Long id, User user) {
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new UserNotFoundException("Not found user by id=" + id);
        }
        User userInDB = searchResult.get();
        userInDB.setPassword(user.getPassword());
        userRepository.save(userInDB);
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Not found user by id=" + id);
        }
        userRepository.deleteById(id);
    }

    public List<LectureDTO> findAllLecturesByUserId(Long id) {
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new UserNotFoundException("Not found user by id=" + id);
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