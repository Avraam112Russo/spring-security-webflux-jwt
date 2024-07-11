package com.n1nt3nd0.reactivesecurityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AuthResponseDto {
    private Long userId;
    private String token;
    private Date issuedAt;
    private Date expiresAt;
}
