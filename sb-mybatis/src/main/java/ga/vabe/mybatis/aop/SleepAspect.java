package ga.vabe.mybatis.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.LongAdder;

@Slf4j
@Component
@Aspect
public class SleepAspect {

    long returnMillis = 200L;
    int guestBound = 1;
    LongAdder guest = new LongAdder();

    @Pointcut("execution(* ga.vabe.mybatis.controller.AopController.index())" +
            "|| execution(* ga.vabe.mybatis.controller.AopController.add())")
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
        guest.increment();
        Object obj = null;
        try {
            obj = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long elapse = now() - begin;
        if (guest.intValue() >= guestBound) {
            long waitMillis = returnMillis - elapse;
            if (waitMillis > 0) {
                log.debug("方法: {} 真实调用时间: {}ms, 准备延时 {}ms 返回", joinPoint.getSignature().getName(), elapse, waitMillis);
                delay(waitMillis);
            }
        }
        guest.decrement();
        return obj;
    }

}
