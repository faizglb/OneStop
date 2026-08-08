package com.acea.backend.dto.admin;

import com.acea.backend.domain.enums.Role;
import lombok.Data;

@Data
public class AdminUserUpdateDto {
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
    private String accountTier;
    private Boolean active;
    private Boolean suspended;
    private String suspensionReason;
}
