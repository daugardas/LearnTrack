package com.learntrack.authorizationserver.models;

import lombok.Data;

@Data
public class UserResponseDTO {
    private final Long id;

    private final String username;

    public UserResponseDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername());
    }
}

