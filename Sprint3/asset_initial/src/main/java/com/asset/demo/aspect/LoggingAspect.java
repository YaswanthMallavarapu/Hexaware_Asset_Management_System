package com.asset.demo.aspect;

//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Aspect
//@Component
//public class LoggingAspect {
//
//    @Around("execution(* com.asset.demo.controller..*(..)) || execution(* com.asset.demo.service..*(..))")
//    public Object logMethods(ProceedingJoinPoint joinPoint) throws Throwable {
//
//        String className = joinPoint.getTarget().getClass().getSimpleName();
//        String methodName = joinPoint.getSignature().getName();
//
//        log.atInfo().log("Entered {}() in {}", methodName, className);
//
//        try {
//            Object result = joinPoint.proceed();
//
//            log.atInfo().log("Returning from {}() in {}", methodName, className);
//            return result;
//
//        } catch (Throwable ex) {
//
//
//            log.atWarn().setCause(ex)
//                    .log("Exception in {}() in {}", methodName, className);
//
//            throw ex;
//        }
//    }
//}