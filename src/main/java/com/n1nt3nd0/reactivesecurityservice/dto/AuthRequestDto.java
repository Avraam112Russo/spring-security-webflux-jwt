package com.n1nt3nd0.reactivesecurityservice.dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String username;
    private String password;
}
