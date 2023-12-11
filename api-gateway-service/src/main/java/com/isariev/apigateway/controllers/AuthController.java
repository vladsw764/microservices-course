package com.isariev.apigateway.controllers;

import com.isariev.apigateway.dtos.ConfirmRequest;
import com.isariev.apigateway.dtos.SignInRequest;
import com.isariev.apigateway.dtos.SignUpRequestDto;
import com.isariev.apigateway.service.userauth.UserAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserAuthService authService;

    public AuthController(UserAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public Mono<String> signUp(@RequestBody SignUpRequestDto signUpRequest) {
        authService.registerUser(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword());
        return Mono.create(r -> r.success("User signed up successfully!"));
    }

    @PostMapping("/confirm")
    public Mono<String> confirmEmailVerification(@RequestBody ConfirmRequest confirmRequest) {
        authService.confirmUserAccount(confirmRequest.username(), confirmRequest.code());
        return Mono.create(response -> response.success("Email verification successful!"));
    }


    @PostMapping("/signin")
    public Mono<Void> signIn(@RequestBody SignInRequest signInRequest, ServerHttpResponse response) {
        return authService.signinUser(signInRequest.username(), signInRequest.password(), response);
    }

}
