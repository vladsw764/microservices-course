package com.isariev.apigateway.controller;

import com.isariev.apigateway.dto.UserInfoDto;
import com.isariev.apigateway.service.UserInfoService;
import com.isariev.apigateway.service.UserInfoServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/userinfo")
public class UserDataController {

    private final UserInfoService userService;

    public UserDataController(UserInfoServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public Mono<UserInfoDto> getUserInfo(Authentication authentication) {
        return userService.getUserInfo(authentication);
    }

    @GetMapping("/id")
    public String getUserId(Authentication authentication) {
        return userService.getUserId(authentication);
    }
}
