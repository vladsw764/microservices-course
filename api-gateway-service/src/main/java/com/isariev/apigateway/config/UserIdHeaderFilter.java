package com.isariev.apigateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;


@Component
public class UserIdHeaderFilter extends AbstractGatewayFilterFactory<UserIdHeaderFilter.NameConfig> {

    public UserIdHeaderFilter() {
        super(NameConfig.class);
    }

    @Override
    public GatewayFilter apply(NameConfig config) {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt jwt) {
                        String userId = jwt.getClaim("sub");
                        ServerHttpRequest request = exchange.getRequest().mutate()
                                .headers(httpHeaders -> httpHeaders.set("X-User-Id", userId))
                                .build();
                        return chain.filter(exchange.mutate().request(request).build());
                    }
                    return chain.filter(exchange);
                });
    }
}

