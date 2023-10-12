package service.ms_search_engine.aopTest;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class MyAspect {

    @Before("execution(* service.ms_search_engine.controllers.TestController.*(..))")
    public void before(){
        System.out.println("Before Aspect invoke");
    }

    @After("execution(* service.ms_search_engine.controllers.TestController.*(..))")
    public void after(){
        System.out.println("Before Aspect invoke");
    }

    @Around("execution(* service.ms_search_engine.controllers.TestController.*(..))"+
            " || execution(* service.ms_search_engine.controllers.SpectrumController.*(..))"+
            " || execution(* service.ms_search_engine.jdbcTest.jdbcTestController.*(..))"
    )
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("Starting timer");
        Date startDate = new Date();

        Object obj = pjp.proceed();
        System.out.println("End timer");
        Date endDate = new Date();
        long spendTime = endDate.getTime() - startDate.getTime();

        System.out.println("Spend time: " + spendTime + " ms" );
        return  obj;
    }



}
