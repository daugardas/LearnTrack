package com.learntrack.authorizationserver.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAddRoleRequestDTO {
    private Long roleId;

    public UserAddRoleRequestDTO(Long roleId) {
        this.roleId = roleId;
    }

    public Long getRoleId() {
        return roleId;
    }
}
