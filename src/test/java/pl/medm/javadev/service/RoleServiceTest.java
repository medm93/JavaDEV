package pl.medm.javadev.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.medm.javadev.model.dto.RoleDTO;
import pl.medm.javadev.model.entity.Role;
import pl.medm.javadev.repository.RoleRepository;
import pl.medm.javadev.utils.exception.RoleExistsException;
import pl.medm.javadev.utils.mapper.RoleMapper;

import javax.management.relation.RoleNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    private RoleService roleService;

    private RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    @BeforeEach
    void setup() {
        roleService = new RoleService(roleRepository);
    }

    //FIND ALL ROLES
    @Test
    void testWhenFindAllRoles() {
        List<Role> roles = Arrays.asList(new Role("ROLE_ADMIN"), new Role("ROLE_USER"));
        List<RoleDTO> expected = roles.stream().map(roleMapper::roleToRoleDTO).collect(Collectors.toList());
        when(roleRepository.findAll()).thenReturn(roles);

        List<RoleDTO> actual = roleService.findAllRoles();
        Assertions.assertIterableEquals(expected, actual);
        verify(roleRepository, times(1)).findAll();
    }

    //CREATE ROLE
    @Test
    void testWhenCreateRoleThenRoleExists() {
        String roleName = "ROLE_MODERATOR";
        Role role = new Role(roleName);
        when(roleRepository.existsRole(roleName)).thenReturn(true);

        Throwable exception = assertThrows(RoleExistsException.class, () ->
                roleService.createRole(role)
        );
            Assertions.assertEquals("Role exists!", exception.getMessage());
        verify(roleRepository, times(1)).existsRole(roleName);
        verify(roleRepository, times(0)).save(role);
    }

    @Test
    void testWhenCreateRoleThenRoleNotExists() {
        Role role = new Role("ROLE_MODERATOR");
        when(roleRepository.existsRole("ROLE_MODERATOR")).thenReturn(false);

        RoleDTO expected = roleMapper.roleToRoleDTO(role);
        RoleDTO actual = roleService.createRole(role);
        Assertions.assertEquals(expected, actual);
        verify(roleRepository, times(1)).existsRole("ROLE_MODERATOR");
        verify(roleRepository, times(1)).save(role);
    }

    //FIND ROLE BY ID
    @Test
    void testWhenFindRoleByIdThenRoleFound() {
        Role role = new Role("ROLE_MODERATOR");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        RoleDTO expected = roleMapper.roleToRoleDTO(role);
        RoleDTO actual = roleService.findRoleById(1L);
        Assertions.assertEquals(expected, actual);
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    void testWhenFindRoleByIdThenRoleNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(RoleNotFoundException.class, () ->
                roleService.findRoleById(1L)
        );
        Assertions.assertEquals("Role not found!", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
    }

    //UPDATE ROLE BY ID
    @Test
    void testWhenUpdateRoleByIdThenRoleFound() {
        Role role = new Role("ROLE_USER");
        Role updated = new Role("ROLE_MODERATOR");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        RoleDTO expected = roleMapper.roleToRoleDTO(role);
        RoleDTO actual = roleService.updateRoleById(1L, updated);
        Assertions.assertEquals(expected, actual);
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testWhenUpdateRoleByIdThenThenRoleNotFound() {
        Role role = new Role("ROLE_USER");
        Role updated = new Role("ROLE_MODERATOR");
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(RoleNotFoundException.class, () ->
                roleService.updateRoleById(1L, updated)
        );
        Assertions.assertEquals("Role not found!", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(0)).save(role);
    }

    //DELETE ROLE BY ID
    @Test
    void testWhenDeleteRoleByIdThenRoleFound() {
        Role role = new Role("ROLE_USER");
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RoleDTO expected = roleMapper.roleToRoleDTO(role);
        RoleDTO actual = roleService.deleteRoleById(1L);
        Assertions.assertEquals(expected, actual);
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    void testWhenDeleteRoleByIdThenRoleNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(RoleNotFoundException.class, () ->
                roleService.deleteRoleById(1L)
        );
        Assertions.assertEquals("Role not found!", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).deleteById(1L);
    }
}
