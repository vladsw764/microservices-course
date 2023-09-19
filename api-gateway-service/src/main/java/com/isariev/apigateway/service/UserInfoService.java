package com.isariev.apigateway.service;

import com.isariev.apigateway.dto.UserInfoDto;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public interface UserInfoService {
    Mono<UserInfoDto> getUserInfo(Authentication authentication);
    String getUserId(Authentication authentication);
}

