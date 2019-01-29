package pl.medm.javadev.utils.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import pl.medm.javadev.model.dto.RoleDTO;
import pl.medm.javadev.model.entity.Role;

@SpringBootTest(classes = LectureMapperImpl.class)
class RoleMapperTest {

    private RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    @Test
    void testWhenRoleToRoleDTOThenCorrect() {
        Role role = new Role("ROLE_USER");
        RoleDTO roleDTO = roleMapper.roleToRoleDTO(role);

        Assertions.assertEquals(role.getRole(), roleDTO.getRole());
    }

    @Test
    void testWhenRoleDTOToRoleThenCorrect() {
        RoleDTO roleDTO = new RoleDTO("ROLE_USER");
        Role role = roleMapper.roleDTOToRole(roleDTO);

        Assertions.assertEquals(roleDTO.getRole(), role.getRole());
    }
}
