package com.n1nt3nd0.reactivesecurityservice.http.REST;

import com.n1nt3nd0.reactivesecurityservice.dto.AuthRequestDto;
import com.n1nt3nd0.reactivesecurityservice.dto.AuthResponseDto;
import com.n1nt3nd0.reactivesecurityservice.dto.UserSecurityDto;
import com.n1nt3nd0.reactivesecurityservice.entity.UserEntity;
import com.n1nt3nd0.reactivesecurityservice.mapper.UserSecurityMapper;
import com.n1nt3nd0.reactivesecurityservice.security.CustomPrincipal;
import com.n1nt3nd0.reactivesecurityservice.security.SecurityService;
import com.n1nt3nd0.reactivesecurityservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestControllerV1 {
    private final SecurityService securityService;
    private final UserService userService;
    private final UserSecurityMapper userSecurityMapper;

    @PostMapping("/register")
    public Mono<UserSecurityDto> registration(@RequestBody UserSecurityDto userSecurityDto) {
        UserEntity user = userSecurityMapper.toUserSecurity(userSecurityDto);
        return userService.registerUser(user).map(savedUser -> userSecurityMapper.toUserSecurityDto(savedUser));
    }
    @PostMapping("/login")
    public Mono<AuthResponseDto> login(@RequestBody AuthRequestDto dto){
        return securityService.authenticate(dto.getUsername(), dto.getPassword())
                .flatMap(tokenDetails -> {
                   return Mono.just(AuthResponseDto.builder()
                                    .token(tokenDetails.getToken())
                                    .userId(tokenDetails.getUserId())
                                    .issuedAt(tokenDetails.getIssuedAt())
                                    .expiresAt(tokenDetails.getExpiresAt())
                            .build());
                });
    }
    @GetMapping("/info")
    public Mono<UserSecurityDto> getInfoAboutUser(Authentication authentication){
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return userService.getUserById(customPrincipal.getId())
                .map(user -> userSecurityMapper.toUserSecurityDto(user));
    }
}
