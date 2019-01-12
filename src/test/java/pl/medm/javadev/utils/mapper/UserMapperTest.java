package pl.medm.javadev.utils.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.medm.javadev.model.dto.UserDTO;
import pl.medm.javadev.model.entity.User;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserMapperImpl.class)
public class UserMapperTest {

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void givenUserToUserDTO_whenMaps_thenCorrect() {
        User user = new User();
        user.setFirstName("Jan");
        user.setLastName("Nowak");
        user.setEmail("jan.nowak@gmail.com");
        user.setPassword("zaq1@WSX");
        user.setYearOfStudy("1");
        user.setFieldOfStudy("Informatyka");
        user.setIndexNumber("123456");
        UserDTO userDTO = userMapper.userToUserDTO(user);

        assertEquals(user.getFirstName(), userDTO.getFirstName());
        assertEquals(user.getLastName(), userDTO.getLastName());
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(user.getPassword(), userDTO.getPassword());
        assertEquals(user.getYearOfStudy(), userDTO.getYearOfStudy());
        assertEquals(user.getFieldOfStudy(), userDTO.getFieldOfStudy());
        assertEquals(user.getIndexNumber(), userDTO.getIndexNumber());
    }

    @Test
    public void givenUserDTOToUser_whenMaps_thenCorrect() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jan");
        userDTO.setLastName("Nowak");
        userDTO.setEmail("jan.nowak@gmail.com");
        userDTO.setPassword("zaq1@WSX");
        userDTO.setYearOfStudy("1");
        userDTO.setFieldOfStudy("Informatyka");
        userDTO.setIndexNumber("123456");
        User user = userMapper.userDTOToUser(userDTO);

        assertEquals(userDTO.getFirstName(), user.getFirstName());
        assertEquals(userDTO.getLastName(), user.getLastName());
        assertEquals(userDTO.getEmail(), user.getEmail());
        assertEquals(userDTO.getPassword(), user.getPassword());
        assertEquals(userDTO.getYearOfStudy(), user.getYearOfStudy());
        assertEquals(userDTO.getFieldOfStudy(), user.getFieldOfStudy());
        assertEquals(userDTO.getIndexNumber(), user.getIndexNumber());
    }
}
