package com.example.cepapi.config;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RateLimitAspect {

    private final Bucket bucket;

    public RateLimitAspect(Bucket bucket) {
        this.bucket = bucket;
    }

    @Around("execution(* com.example.cepapi.controller.CepController.getCepInfo(..))")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        }

        HttpServletResponse response =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getResponse();
        if (response != null) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Muitas requisicoes. Tente novamente em instantes.\"}");
        }
        return null;
    }
}