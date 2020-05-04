package ga.vabe.mybatis.aop;

import ga.vabe.mybatis.common.AppContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class SleepAspect {

    @Autowired
    private AppContext context;

    @Pointcut("execution(* ga.vabe.mybatis.controller.AopController.index())" +
            "|| execution(* ga.vabe.mybatis.controller.IndexController.add(..))")
    public void pointCut() {
        // go away ~
    }

    private long now() {
        return System.currentTimeMillis();
    }

    private void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Around(value = "pointCut()")
    public Object doAroundAdvice(ProceedingJoinPoint joinPoint) {
        long begin = now();
        context.guestIncrement();
        Object obj = null;
        try {
            obj = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long elapse = now() - begin;
        if (context.overGuest()) {
            long waitMillis = context.returnMillis - elapse;
            if (waitMillis > 0) {
                log.debug("方法: {}.{} 执行用时: {}ms, 准备延时 {}ms 返回",
                        joinPoint.getTarget().getClass().getSimpleName(),
                        joinPoint.getSignature().getName(), elapse, waitMillis);
                delay(waitMillis);
            }
        }
        context.guestDecrement();
        return obj;
    }

}
