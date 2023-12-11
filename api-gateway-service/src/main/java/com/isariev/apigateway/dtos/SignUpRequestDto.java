package com.isariev.apigateway.dtos;

import lombok.Getter;

@Getter
public class SignUpRequestDto {

    private String username;
    private String password;
    private String email;

    public SignUpRequestDto(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public SignUpRequestDto() {
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
