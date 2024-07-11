package com.n1nt3nd0.reactivesecurityservice.repository;

import com.n1nt3nd0.reactivesecurityservice.entity.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserSecurityRepository extends R2dbcRepository<UserEntity, Long> {
    Mono<UserEntity> findByUsername(String username);
}
