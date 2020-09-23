package ga.vabe.test.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(name = "app.aspect.request_time", havingValue = "open")
public class RequestTimeAspect {

    private String className = this.getClass().getSimpleName();

    @Pointcut("within(ga.vabe.test.controller..*)")
    private void controller() {

    }

    @Before("controller()")
    public void beforeController() {
        log.info("{} ==> beforeController", className);
    }

    @After("controller()")
    public void afterController() {
        log.info("{} ==> afterController", className);
    }

}
