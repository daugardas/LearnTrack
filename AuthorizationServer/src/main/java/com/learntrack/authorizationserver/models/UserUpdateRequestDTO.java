package com.learntrack.authorizationserver.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateRequestDTO {
    @NotBlank
    private String username;

    public UserUpdateRequestDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
