package com.learntrack.authorizationserver.web.controllers;

import com.learntrack.authorizationserver.models.*;
import com.learntrack.authorizationserver.web.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user/roles")
public class RoleController {
    private final RoleService roleService;
    private final Logger logger = LoggerFactory.getLogger(RoleController.class);

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> findAllRolesForUser(@AuthenticationPrincipal Jwt jwt) {
        logger.info("Finding all roles for user {}", jwt.getSubject());
        Iterable<Role> roles = roleService.findAllRolesForUser(Long.parseLong(jwt.getSubject()));
        List<RoleResponseDTO> roleResponseDTOs = new ArrayList<>();
        for (Role role : roles) {
            roleResponseDTOs.add(new RoleResponseDTO(role.getId(), role.getName()));
        }
        return ResponseEntity.ok(roleResponseDTOs);
    }

    @PutMapping
    public ResponseEntity<UserResponseDTO> addRoleToUser(@AuthenticationPrincipal Jwt jwt, @RequestBody UserAddRoleRequestDTO userAddRoleRequestDTO) {
        logger.info("Adding role {} to user {}", userAddRoleRequestDTO.getRoleId(), jwt.getSubject());
        User userWithRole = roleService.addRoleToUser(Long.parseLong(jwt.getSubject()), userAddRoleRequestDTO.getRoleId());
        return ResponseEntity.ok().body(UserResponseDTO.from(userWithRole));
    }
}
