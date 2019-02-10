package pl.medm.javadev.controller.rest;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.medm.javadev.model.dto.RoleDTO;
import pl.medm.javadev.service.RoleService;
import pl.medm.javadev.utils.exception.ConflictException;
import pl.medm.javadev.utils.exception.ForbiddenException;
import pl.medm.javadev.utils.exception.NotFoundException;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Log4j2
public class RoleRestController {

    private final RoleService roleService;

    @Autowired
    public RoleRestController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> findAllRoles() {
        List<RoleDTO> roles = roleService.findAllRoles();
        log.info("Received {} results for roles search", roles.size());
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> createRole(@Valid @RequestBody RoleDTO dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.warn("Role validate failed");
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }
            dto = roleService.createRole(dto);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("{id}")
                    .buildAndExpand(dto.getId())
                    .toUri();
            log.info("Role [id={}] created", dto.getId());
            return ResponseEntity.created(location).body(dto);
        } catch (ConflictException e) {
            log.error("Conflict! Role [{}] already exists", dto.getRole(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> findRoleById(@PathVariable Long id) {
        try {
            log.info("Role [id={} found", id);
            return ResponseEntity.ok(roleService.findRoleById(id));
        } catch (NotFoundException e) {
            log.error("Role [id={}] not found", id, e);
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> updateRoleById(@PathVariable Long id, @Valid @RequestBody RoleDTO dto,
                                                 BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.warn("Role [id={}] not found", id, result);
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }
            roleService.updateRoleById(id, dto);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            log.error("Role [id={}] not found", id, e);
            return ResponseEntity.notFound().build();
        } catch (ForbiddenException e) {
            log.error("Updating admin role is not allowed", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> deleteRoleById(@PathVariable Long id) {
        try {
            roleService.deleteRoleById(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            log.error("Role [id={}] not found", id, e);
            return ResponseEntity.notFound().build();
        } catch (ForbiddenException e) {
            log.error("Deleting admin role is not allowed", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
