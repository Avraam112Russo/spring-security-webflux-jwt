package com.n1nt3nd0.reactivesecurityservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("t_users_security_service")
public class UserEntity {
    @Id
    private long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private UserRole role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ToString.Include(name = "password")
    public String maskPassword() {
        return "************";
    }
}
