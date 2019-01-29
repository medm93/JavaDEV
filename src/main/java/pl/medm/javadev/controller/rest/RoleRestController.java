package pl.medm.javadev.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.medm.javadev.model.dto.RoleDTO;
import pl.medm.javadev.model.entity.Role;
import pl.medm.javadev.service.RoleService;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/roles")
public class RoleRestController {

    private final RoleService roleService;

    @Autowired
    public RoleRestController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> findAllRoles() {
        return ResponseEntity.ok(roleService.findAllRoles());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> createRole(@Valid @RequestBody Role role, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        RoleDTO roleDTO = roleService.createRole(role);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(roleDTO.getId())
                .toUri();
        return ResponseEntity.created(location).body(role);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> findRoleById(@PathVariable long id) {
        return ResponseEntity.ok(roleService.findRoleById(id));
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> updateRoleById(@PathVariable long id, @Valid @RequestBody Role role, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        roleService.updateRoleById(id, role);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> deleteRoleById(@PathVariable long id) {
        roleService.deleteRoleById(id);
        return ResponseEntity.noContent().build();
    }
}
