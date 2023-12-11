package com.isariev.apigateway.service.userauth;

import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

public interface UserAuthService {
    void registerUser(String username, String email, String password);

    void confirmUserAccount(String username, String confirmationCode);

    Mono<Void> signinUser(String username, String password, ServerHttpResponse response);
}
