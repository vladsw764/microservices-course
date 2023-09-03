package com.isariev.orderservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CircuitBreakerConfiguration {

    private final TimeLimiterRegistry limiterRegistry;

    public CircuitBreakerConfiguration(TimeLimiterRegistry limiterRegistry) {
        this.limiterRegistry = limiterRegistry;
    }

    @Bean
    public CircuitBreaker countCircuitBreaker() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .slowCallRateThreshold(65.0f)
                .slowCallDurationThreshold(Duration.ofSeconds(3))
                .build();

        CircuitBreakerRegistry circuitBreakerRegistry =
                CircuitBreakerRegistry.of(circuitBreakerConfig);

        return circuitBreakerRegistry.circuitBreaker("InventoryServiceBasedOnCount");
    }

    @Bean
    public TimeLimiter timeLimiter() {
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(10))
                .build();

        return limiterRegistry.timeLimiter("inventoryTimelimiter", timeLimiterConfig);
    }

    @Bean
    public CircuitBreaker timeCircuitBreaker() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
                .minimumNumberOfCalls(1)
                .slidingWindowSize(10)
                .failureRateThreshold(70.0f)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build();

        CircuitBreakerRegistry circuitBreakerRegistry =
                CircuitBreakerRegistry.of(circuitBreakerConfig);

        return circuitBreakerRegistry.circuitBreaker("InventoryServiceBasedOnTime");
    }
}
