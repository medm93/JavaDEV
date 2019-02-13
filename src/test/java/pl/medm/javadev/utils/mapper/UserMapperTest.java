package pl.medm.javadev.utils.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import pl.medm.javadev.model.dto.UserDTO;
import pl.medm.javadev.model.entity.Role;
import pl.medm.javadev.model.entity.User;

@SpringBootTest(classes = {UserMapperImpl.class, RoleMapperImpl.class})
class UserMapperTest {

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void testWhenUserToUserDTOThenCorrect() {
        User user = new User(1L, "'Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");
        user.getRoles().add(new Role(2L, "ROLE_USER"));

        UserDTO userDTO = userMapper.userToUserDTO(user);

        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
        Assertions.assertEquals(user.getEmail(), userDTO.getEmail());
        Assertions.assertEquals(user.getYearOfStudy(), userDTO.getYearOfStudy());
        Assertions.assertEquals(user.getFieldOfStudy(), userDTO.getFieldOfStudy());
        Assertions.assertEquals(user.getIndexNumber(), userDTO.getIndexNumber());
        Assertions.assertEquals(user.getRoles().iterator().next().getId(), userDTO.getRoles().iterator().next().getId());
        Assertions.assertEquals(user.getRoles().iterator().next().getRole(), userDTO.getRoles().iterator().next().getRole());
    }



    @Test
    void testWhenUserDTOToUserThenCorrect() {
        UserDTO userDTO = new UserDTO(1L, "'Clint", "Barton", "hawkeye@marvel.com", "zaq1@WSX",
                "1", "Automatics", "000001");

        User user = userMapper.userDTOToUser(userDTO);

        Assertions.assertEquals(userDTO.getId(), user.getId());
        Assertions.assertEquals(userDTO.getFirstName(), user.getFirstName());
        Assertions.assertEquals(userDTO.getLastName(), user.getLastName());
        Assertions.assertEquals(userDTO.getEmail(), user.getEmail());
        Assertions.assertEquals(userDTO.getPassword(), user.getPassword());
        Assertions.assertEquals(userDTO.getYearOfStudy(), user.getYearOfStudy());
        Assertions.assertEquals(userDTO.getFieldOfStudy(), user.getFieldOfStudy());
        Assertions.assertEquals(userDTO.getIndexNumber(), user.getIndexNumber());
    }

}
