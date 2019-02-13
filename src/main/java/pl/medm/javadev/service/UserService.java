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
import pl.medm.javadev.utils.exception.ConflictException;
import pl.medm.javadev.utils.exception.NotFoundException;
import pl.medm.javadev.utils.mapper.LectureMapper;
import pl.medm.javadev.utils.mapper.RoleMapper;
import pl.medm.javadev.utils.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final String DEFAULT_ROLE = "ROLE_USER";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final LectureMapper lectureMapper;
    private final RoleMapper roleMapper;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                       UserMapper userMapper, LectureMapper lectureMapper, RoleMapper roleMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.lectureMapper = lectureMapper;
        this.roleMapper = roleMapper;
    }

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO dto) {
        if (userRepository.existsByEmailOrIndexNumber(dto.getEmail(), dto.getIndexNumber())) {
            throw new ConflictException("Conflict! Email " + dto.getEmail() + " or index number " + dto.getIndexNumber()
                    + " is already busy.");
        }
        return addWithDefaultRole(dto);
    }

    public UserDTO findUserById(Long id) {
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("User not found!");
        }
        return searchResult.map(userMapper::userToUserDTO).get();
    }


    public void updateUserById(Long id, UserDTO dto) {
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("User not found!");
        }
        if (userRepository.existsByEmailOrIndexNumber(dto.getEmail(), dto.getIndexNumber())) {
            throw new ConflictException("Conflict! Email " + dto.getEmail() + " or index number " + dto.getIndexNumber()
                    + " is already busy.");
        }
        searchResult.get().setFirstName(dto.getFirstName());
        searchResult.get().setLastName(dto.getLastName());
        searchResult.get().setEmail(dto.getEmail());
        searchResult.get().setYearOfStudy(dto.getYearOfStudy());
        searchResult.get().setFieldOfStudy(dto.getFieldOfStudy());
        searchResult.get().setIndexNumber(dto.getIndexNumber());
        userRepository.save(searchResult.get());
    }

    public void updateUserPasswordById(Long id, UserDTO dto) {
        Optional<User> searchResult = userRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("User not found!");
        }
        searchResult.get().setPassword(dto.getPassword());
        userRepository.save(searchResult.get());
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found!");
        }
        userRepository.deleteById(id);
    }

    public List<LectureDTO> findAllUserLecturesById(Long id) {
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

    private UserDTO addWithDefaultRole(UserDTO dto) {
        User user = userMapper.userDTOToUser(dto);
        Role defaultRole = roleRepository.findByRole(DEFAULT_ROLE);
        user.getRoles().add(defaultRole);
        String passwordHash = passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordHash);
        user = userRepository.save(user);
        dto = userMapper.userToUserDTO(user);
        return dto;
    }
}