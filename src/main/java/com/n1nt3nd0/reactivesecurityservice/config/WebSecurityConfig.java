package com.n1nt3nd0.reactivesecurityservice.config;

import com.n1nt3nd0.reactivesecurityservice.security.BearerTokenServerAuthenticationConverter;
import com.n1nt3nd0.reactivesecurityservice.security.CustomAuthenticationManager;
import com.n1nt3nd0.reactivesecurityservice.security.JwtHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
@Slf4j
public class WebSecurityConfig {

    @Value("${jwt.secret}")
    private String secret;

    private final String [] PUBLIC_ROUTES = {"/api/v1/auth/register", "/api/v1/auth/login"};
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, CustomAuthenticationManager authenticationManager) {
        return http
                .csrf(csrfSpec -> csrfSpec.disable()) // TODO
                .authorizeExchange(request -> {
                    request.pathMatchers(HttpMethod.OPTIONS).permitAll();
                    request.pathMatchers(PUBLIC_ROUTES).permitAll();
                    request.anyExchange().authenticated();
                })
                .exceptionHandling(exceptionHandler -> {
                    exceptionHandler.authenticationEntryPoint((serverWebExchange, e) -> {
                        log.error("IN securityWebFilterChain -> UNAUTHORIZED error: {}", e.getMessage());
                        // if the user has not submitted a credentials to entryPoint
                        return Mono.fromRunnable(() -> serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                    });
                    exceptionHandler.accessDeniedHandler((serverWebExchange, e) -> {
                        log.error("IN securityWebFilterChain -> FORBIDDEN error: {}", e.getMessage());
                        // if the user access denied
                        return Mono.fromRunnable(() -> serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                    });
                })
                .addFilterAt(
                        bearerAuthFilter(authenticationManager),
                        SecurityWebFiltersOrder.AUTHENTICATION // order in the filter chain
                )
                .build();
    }

    // added a filter that will check the token
    private AuthenticationWebFilter bearerAuthFilter( CustomAuthenticationManager authenticationManager) {
        AuthenticationWebFilter bearerAuthenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        bearerAuthenticationWebFilter.setServerAuthenticationConverter(
                new BearerTokenServerAuthenticationConverter( new JwtHandler(secret) )
        );
        bearerAuthenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**")); // this filter will handle any request
        return bearerAuthenticationWebFilter;
    }
}
