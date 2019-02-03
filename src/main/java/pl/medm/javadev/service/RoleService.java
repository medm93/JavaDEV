package pl.medm.javadev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.medm.javadev.model.dto.RoleDTO;
import pl.medm.javadev.model.entity.Role;
import pl.medm.javadev.repository.RoleRepository;
import pl.medm.javadev.utils.exception.*;
import pl.medm.javadev.utils.mapper.RoleMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public List<RoleDTO> findAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::roleToRoleDTO)
                .collect(Collectors.toList());
    }

    public RoleDTO createRole(Role role) {
        if (roleRepository.existsByRole(role.getRole())) {
            throw new ConflictException("Role conflict!");
        }
        roleRepository.save(role);
        return roleMapper.roleToRoleDTO(role);
    }

    public RoleDTO findRoleById(long id) {
        Optional<Role> searchResult = roleRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("Role not found!");
        }
        return searchResult
                .map(roleMapper::roleToRoleDTO)
                .get();
    }

    public void updateRoleById(long id, Role role) {
        Optional<Role> searchResult = roleRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("Role not found!");
        }
        if (roleRepository.existsByRole(role.getRole())) {
            throw new ConflictException("Role conflict!");
        }
        if (searchResult.get().getRole().equals("ROLE_ADMIN")) {
            throw new ForbiddenException("Forbidden!");
        }
        searchResult.get().setRole(role.getRole());
        roleRepository.save(searchResult.get());
    }

    public void deleteRoleById(long id) {
        Optional<Role> searchResult = roleRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("Role not found!");
        }
        if (searchResult.get().getRole().equals("ROLE_ADMIN")) {
            throw new ForbiddenException("Forbidden!");
        }
        roleRepository.deleteById(id);
    }
}
