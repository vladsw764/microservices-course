package com.isariev.apigateway.service.userauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import software.amazon.awssdk.utils.ImmutableMap;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    @Value("${cognito.idp.client.id}")
    private String clientId;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthServiceImpl.class);

    public UserAuthServiceImpl(CognitoIdentityProviderClient cognitoIdentityProviderClient) {
        this.cognitoIdentityProviderClient = cognitoIdentityProviderClient;
    }

    @Override
    public void registerUser(String username, String email, String password) {
        String userSub;
        try {
            SignUpResponse response = processSignUp(username, password, email);
            userSub = response.userSub();
        } catch (Exception e) {
            throw new RuntimeException("An Error occurred during the sign up processing!");
        }
        LOGGER.info("User signed up successfully: {}, {}", username, userSub);
    }

    @Override
    public void confirmUserAccount(String username, String confirmationCode) {
        processConfirmation(username, confirmationCode);
        LOGGER.info("User account confirmed for: {}", username);
    }

    @Override
    public Mono<Void> signinUser(String username, String password, ServerHttpResponse response) {
        return Mono.fromCallable(() -> processSignIn(username, password))
                .doOnNext(authResponse -> response.getHeaders().add("Authorization",
                        "Bearer " + authResponse.authenticationResult().accessToken()))
                .doOnSuccess(authResponse -> LOGGER.info("User was authenticated with username: {}", username))
                .then();
    }


    private InitiateAuthResponse processSignIn(String username, String password) {
        InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                .clientId(clientId)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(ImmutableMap.of(
                        "USERNAME", username,
                        "PASSWORD", password
                ))
                .build();

        return cognitoIdentityProviderClient.initiateAuth(authRequest);
    }

    private void processConfirmation(String username, String confirmationCode) {
        ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
                .clientId(clientId)
                .username(username)
                .confirmationCode(confirmationCode)
                .build();
        cognitoIdentityProviderClient.confirmSignUp(confirmSignUpRequest);
    }

    private SignUpResponse processSignUp(String username, String password, String email) {
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(username)
                .password(password)
                .userAttributes(
                        AttributeType.builder().name("email").value(email).build()
                )
                .clientId(clientId)
                .build();
        return cognitoIdentityProviderClient.signUp(signUpRequest);
    }
}
