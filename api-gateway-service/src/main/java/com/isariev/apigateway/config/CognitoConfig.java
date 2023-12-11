package com.isariev.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
public class CognitoConfig {

    @Value("${aws.cognito.course.common.access.key}")
    private String accessKey;

    @Value("${aws.cognito.course.secret.access.key}")
    private String secretAccessKey;

    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProviderClient() {
        AwsCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretAccessKey);
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCredentials);

        return CognitoIdentityProviderClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.US_EAST_1)
                .build();
    }
}
