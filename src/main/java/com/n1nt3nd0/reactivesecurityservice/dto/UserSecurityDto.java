package com.n1nt3nd0.reactivesecurityservice.dto;

import com.n1nt3nd0.reactivesecurityservice.entity.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserSecurityDto {
    private long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private UserRole role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
