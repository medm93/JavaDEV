package pl.medm.javadev.utils.mapper;

import org.mapstruct.Mapper;
import pl.medm.javadev.model.dto.RoleDTO;
import pl.medm.javadev.model.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDTO roleToRoleDTO(Role role);
    Role roleDTOToRole(RoleDTO roleDTO);
}
