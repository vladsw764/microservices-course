package com.isariev.apigateway.aop;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Log4j2
@Aspect
@Component
public class LoggingControllersAspect {

    private LocalDateTime start;

    @Pointcut("execution(public * (@org.springframework.stereotype.Controller * || @org.springframework.web.bind.annotation.RestController *).*(..))")
    public void allPublicControllerMethods() {
    }

    @Before("allPublicControllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        start = LocalDateTime.now();
        log.info("\u001B[32m" + "Controller: " + methodName + " - start." + "\u001B[0m");
    }

    @After("allPublicControllerMethods()")
    public void logAfter(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        long executionTime = Duration.between(start, LocalDateTime.now()).toMillis();
        log.info("\u001B[32m" + "Controller: " + methodName + " - end. Execution time:" + executionTime + "\u001B[0m");
    }
}