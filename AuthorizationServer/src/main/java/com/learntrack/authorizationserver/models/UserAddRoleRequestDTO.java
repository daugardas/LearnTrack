package com.learntrack.authorizationserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddRoleRequestDTO {
    private Long roleId;

    public Long getRoleId() {
        return roleId;
    }
}
