package com.n1nt3nd0.reactivesecurityservice.security;

import com.n1nt3nd0.reactivesecurityservice.exception.unAuthorizedException.UnAuthorizedException;
import com.n1nt3nd0.reactivesecurityservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {
    private final UserService userService;
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return userService.findUserByUsername(principal.getName())
                .filter(user -> user.isEnabled() == true)
                .switchIfEmpty(Mono.error(new UnAuthorizedException("User disabled.")))
                .map(user -> authentication);
    }
}
