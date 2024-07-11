package com.n1nt3nd0.reactivesecurityservice.service;

import com.n1nt3nd0.reactivesecurityservice.entity.UserEntity;
import com.n1nt3nd0.reactivesecurityservice.entity.UserRole;
import com.n1nt3nd0.reactivesecurityservice.repository.UserSecurityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserSecurityRepository userSecurityRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<UserEntity> registerUser(UserEntity userEntity) {
        return userSecurityRepository.save(
                userEntity.toBuilder()
                        .firstName(userEntity.getFirstName())
                        .lastName(userEntity.getLastName())
                        .username(userEntity.getUsername())
                        .password(passwordEncoder.encode(userEntity.getPassword()))
                        .role(UserRole.USER)
                        .enabled(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        ).doOnSuccess(savedUser -> log.info("User registered successfully: {}", savedUser));
    }
    public Mono<UserEntity> findUserByUsername(String username) {
        return userSecurityRepository.findByUsername(username);
    }
    public Mono<UserEntity> getUserById(Long id) {
        return userSecurityRepository.findById(id);
    }
}
