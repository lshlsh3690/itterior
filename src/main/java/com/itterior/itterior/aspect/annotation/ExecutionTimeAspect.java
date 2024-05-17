package com.itterior.itterior.aspect.annotation;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
public class ExecutionTimeAspect {
    @Around("@annotation(MeasureExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info(joinPoint.getSignature() + " executed in " + (endTime - startTime) + "ms");
        return result;
    }

    @Around("execution(* com.itterior.itterior.util.CustomFileUtil.saveProFile(..))")
    public Object measureExecutionTimeWithMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        System.out.println(joinPoint.getSignature() + " executed in " + (endTime - startTime) + "ms");
        return result;
    }
}