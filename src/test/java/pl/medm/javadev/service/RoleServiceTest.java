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
import pl.medm.javadev.utils.exception.*;
import pl.medm.javadev.utils.mapper.RoleMapper;

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
        roleService = new RoleService(roleRepository, roleMapper);
    }

    //FIND ALL ROLES
    @Test
    void testWhenFindAllRoles() {
        List<Role> roles = Arrays.asList(new Role(1L, "ROLE_ADMIN"), new Role(2L, "ROLE_USER"));
        when(roleRepository.findAll()).thenReturn(roles);

        List<RoleDTO> expected = roles.stream()
                .map(roleMapper::roleToRoleDTO)
                .collect(Collectors.toList());
        List<RoleDTO> actual = roleService.findAllRoles();

        Assertions.assertIterableEquals(expected, actual);
        verify(roleRepository, times(1)).findAll();
    }

    //CREATE ROLE
    @Test
    void testWhenCreateRoleThenRoleConflict() {
        Role role = new Role(1L, "ROLE_MODERATOR");
        when(roleRepository.existsByRole("ROLE_MODERATOR")).thenReturn(true);

        Throwable exception = assertThrows(ConflictException.class, () ->
                roleService.createRole(role)
        );

        Assertions.assertEquals("Role conflict!", exception.getMessage());
        verify(roleRepository, times(1)).existsByRole("ROLE_MODERATOR");
        verify(roleRepository, times(0)).save(role);
    }

    @Test
    void testWhenCreateRoleThenRoleCreated() {
        Role role = new Role(1L, "ROLE_MODERATOR");
        when(roleRepository.existsByRole("ROLE_MODERATOR")).thenReturn(false);

        RoleDTO expected = roleMapper.roleToRoleDTO(role);
        RoleDTO actual = roleService.createRole(role);

        Assertions.assertEquals(expected, actual);
        verify(roleRepository, times(1)).existsByRole("ROLE_MODERATOR");
        verify(roleRepository, times(1)).save(role);
    }

    //FIND ROLE BY ID
    @Test
    void testWhenFindRoleByIdThenRoleFound() {
        Role role = new Role(1L, "ROLE_MODERATOR");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        RoleDTO expected = roleMapper.roleToRoleDTO(role);
        RoleDTO actual = roleService.findRoleById(1L);

        Assertions.assertEquals(expected, actual);
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    void testWhenFindRoleByIdThenRoleNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class, () ->
                roleService.findRoleById(1L)
        );

        Assertions.assertEquals("Role not found!", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
    }

    //UPDATE ROLE BY ID
    @Test
    void testWhenUpdateRoleByIdThenRoleUpdated() {
        Role role = new Role(1L, "ROLE_USER");
        Role updated = new Role(1L, "ROLE_MODERATOR");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        roleService.updateRoleById(1L, updated);
        RoleDTO expected = new RoleDTO(1L, "ROLE_MODERATOR");
        RoleDTO actual = roleService.findRoleById(1L);

        Assertions.assertEquals(expected, actual);
        verify(roleRepository, times(2)).findById(1L);
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testWhenUpdateRoleByIdThenRoleNotFound() {
        Role role = new Role(1L, "ROLE_USER");
        Role updated = new Role(1L, "ROLE_MODERATOR");
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class, () ->
                roleService.updateRoleById(1L, updated)
        );
        Assertions.assertEquals("Role not found!", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(0)).save(role);
    }

    @Test
    void testWhenUpdateRoleByIdThenUpdateIsForbidden() {
        Role role = new Role(1L, "ROLE_ADMIN");
        Role updated = new Role(1L, "ROLE_MODERATOR");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Throwable exception = assertThrows(ForbiddenException.class, () ->
                roleService.updateRoleById(1L, updated)
        );
        Assertions.assertEquals("Forbidden!", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(0)).save(role);
    }

    //DELETE ROLE BY ID
    @Test
    void testWhenDeleteRoleByIdThenRoleDeleted() {
        Role role = new Role(1L, "ROLE_USER");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        roleService.deleteRoleById(1L);
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    void testWhenDeleteRoleByIdThenRoleNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class, () ->
                roleService.deleteRoleById(1L)
        );
        Assertions.assertEquals("Role not found!", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(0)).deleteById(1L);
    }

    @Test
    void testWhenDeleteRoleByIdThenDeleteIsForbidden() {
        Role role = new Role(1L , "ROLE_ADMIN");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Throwable exception = assertThrows(ForbiddenException.class, () ->
                roleService.deleteRoleById(1L)
        );

        Assertions.assertEquals("Forbidden!", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(0)).save(role);
    }
}
