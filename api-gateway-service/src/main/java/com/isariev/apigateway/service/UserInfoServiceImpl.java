package com.isariev.apigateway.service;

import com.isariev.apigateway.dto.UserInfoDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final WebClient webClient;

    public UserInfoServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }


    public Mono<UserInfoDto> getUserInfo(Authentication authentication) {
        String accessToken = getAccessToken(authentication);

        return webClient.get()
                .uri("/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(UserInfoDto.class);
    }

    public String getUserId(Authentication authentication) {
        return getId(authentication);
    }


    private String getAccessToken(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt jwt = jwtAuthenticationToken.getToken();
            return jwt.getTokenValue();
        }
        return null;
    }

    private String getId(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt jwt = jwtAuthenticationToken.getToken();
            return (String) jwt.getClaims().get("sub");
        }
        return null;
    }
}



