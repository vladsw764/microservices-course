package com.isariev.apigateway.dtos;

import lombok.Data;

@Data
public class SignUpRequestDto {

    private String username;
    private String password;
    private String email;
}
