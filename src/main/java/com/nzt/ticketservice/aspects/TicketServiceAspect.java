//package com.walmart.interview.ticketservice.aspects;
//
//import lombok.extern.slf4j.XSlf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//@XSlf4j
//public class TicketServiceAspect {
//
//
//    @Before("execution(* com.walmart.interview.ticketservice.services.TicketService.*(..))")
//    public void before(JoinPoint joinPoint) {
//        if (log.isTraceEnabled()) {
//
//        }
//        if (log.isDebugEnabled()) {
//
//        }
//        if (log.isInfoEnabled()) {
//
//        }
//        if (log.isWarnEnabled()) {
//
//        }
//    }
//
//
//    @AfterReturning(pointcut = "execution(* com.walmart.interview.ticketservice.services.TicketService.*(..))",returning = "retVal")
//    public void after(JoinPoint joinPoint, Object retVal) {
//        if (log.isTraceEnabled()) {
//
//        }
//        if (log.isDebugEnabled()) {
//
//        }
//        if (log.isInfoEnabled()) {
//
//        }
//        if (log.isWarnEnabled()) {
//
//        }
//    }
//
//    @AfterThrowing(pointcut = "execution(* com.walmart.interview.ticketservice.services.*.*(..))", throwing = "ex")
//    public void handleExceptions(Exception ex) {
//        log.error("An error occurred", ex);
//    }
//}
