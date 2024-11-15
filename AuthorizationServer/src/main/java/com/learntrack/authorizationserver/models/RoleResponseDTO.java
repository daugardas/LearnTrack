package com.learntrack.authorizationserver.models;

import lombok.Data;

@Data
public class RoleResponseDTO {
    private final Long id;
    private final String name;

    public RoleResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
