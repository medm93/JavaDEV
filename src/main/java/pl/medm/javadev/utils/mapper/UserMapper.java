package pl.medm.javadev.utils.mapper;

import org.mapstruct.Mapper;
import pl.medm.javadev.model.dto.UserDTO;
import pl.medm.javadev.model.dto.UserPasswordDTO;
import pl.medm.javadev.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToUserDTO(User user);
    UserPasswordDTO userToUserPasswordDTO(User user);
    User userDTOToUser(UserDTO userDTO);
    User userPasswordDTOToUser(UserPasswordDTO userDTO);
}
