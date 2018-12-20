package pl.medm.javadev.utils.mapper;

import org.mapstruct.Mapper;
import pl.medm.javadev.dto.UserDTO;
import pl.medm.javadev.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToUserDTO(User user);
    User userDTOToUser(UserDTO userDTO);
}
