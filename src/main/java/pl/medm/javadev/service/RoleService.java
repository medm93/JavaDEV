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

    public RoleDTO createRole(RoleDTO dto) {
        if (roleRepository.existsByRole(dto.getRole())) {
            throw new ConflictException("Conflict! Role '" + dto.getRole() + "' already exists.");
        }
        Role role = roleMapper.roleDTOToRole(dto);
        role = roleRepository.save(role);
        dto.setId(role.getId());
        return dto;
    }

    public RoleDTO findRoleById(Long id) {
        Optional<Role> searchResult = roleRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("Role not found!");
        }
        return searchResult
                .map(roleMapper::roleToRoleDTO)
                .get();
    }

    public void updateRoleById(Long id, RoleDTO dto) {
        Optional<Role> searchResult = roleRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("Role not found!");
        }
        if (roleRepository.existsByRole(dto.getRole())) {
            throw new ConflictException("Conflict! Role: '" + dto.getRole() + "' already exists.");
        }
        if (searchResult.get().getRole().equals("ROLE_ADMIN")) {
            throw new ForbiddenException("Updating role 'ROLE_ADMIN' is not allowed!");
        }
        searchResult.get().setRole(dto.getRole());
        roleRepository.save(searchResult.get());
    }

    public void deleteRoleById(Long id) {
        Optional<Role> searchResult = roleRepository.findById(id);
        if (!searchResult.isPresent()) {
            throw new NotFoundException("Role not found!");
        }
        if (searchResult.get().getRole().equals("ROLE_ADMIN")) {
            throw new ForbiddenException("Deleting role 'ROLE_ADMIN' is not allowed!");
        }
        roleRepository.deleteById(id);
    }
}
