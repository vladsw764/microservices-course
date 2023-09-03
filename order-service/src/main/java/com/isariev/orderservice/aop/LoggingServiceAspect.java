package com.isariev.orderservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Aspect
@Slf4j
@Component
public class LoggingServiceAspect {

    @Pointcut("execution(public * com.isariev.orderservice.service.OrderService.*(..))")
    public void orderServiceMethods() {
    }

    @Around("orderServiceMethods()")
    public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.debug("\u001B[91m" + "Service: " + methodName + " - start. Args count - {}" + "\u001B[0m", args.length);

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            Object outputValue;
            if (result != null) {
                if (result instanceof Collection) {
                    outputValue = "Collection size - " + ((Collection<?>) result).size();
                } else if (result instanceof byte[]) {
                    outputValue = "File as byte[]";
                } else {
                    outputValue = result;
                }
                log.debug("\u001B[91m" + "Service: " + methodName + " - end. Returns - {}. Duration - {} ms" + "\u001B[0m", outputValue, executionTime);
            } else {
                log.debug("\u001B[91m" + "Service: " + methodName + " - end. Duration - {} ms" + "\u001B[0m", executionTime);
            }

            return result;
        } catch (Exception e) {
            log.error("\u001B[91m" + "Service: " + methodName + " - Exception thrown: " + e.getMessage() + "\u001B[0m");
            throw e;
        }
    }
}
