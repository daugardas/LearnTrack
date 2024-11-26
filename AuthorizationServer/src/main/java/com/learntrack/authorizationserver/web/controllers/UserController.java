package com.learntrack.authorizationserver.web.controllers;

import com.learntrack.authorizationserver.models.User;
import com.learntrack.authorizationserver.models.UserResponseDTO;
import com.learntrack.authorizationserver.models.UserUpdateRequestDTO;
import com.learntrack.authorizationserver.web.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequestDTO userUpdateDTO) {
        User updatedUser = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok(UserResponseDTO.from(updatedUser));
    }

}
