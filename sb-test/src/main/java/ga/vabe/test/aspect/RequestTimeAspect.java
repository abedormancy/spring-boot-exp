package ga.vabe.test.aspect;

import ga.vabe.test.annotation.OperationLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(name = "app.aspect.request_time", havingValue = "open")
public class RequestTimeAspect {

    private static Map<String, LogAnnotationSpElParse> mapper = new ConcurrentHashMap<>();

    private static Pattern PATTERN = Pattern.compile("[$^]\\(\\s*(#.+?)\\s*\\)");

    private String className = this.getClass().getSimpleName();

    public RequestTimeAspect() {
        System.out.println("time aspect started...");
    }

    @Pointcut("within(ga.vabe.test.controller..*)")
    private void controller() {

    }

    // @Before("controller()")
    // public void beforeController() {
    //     log.info("{} ==> beforeController", className);
    // }
    //
    // @After("controller()")
    // public void afterController() {
    //     log.info("{} ==> afterController", className);
    // }

    @Around(value = "controller()")
    public Object aroundController(ProceedingJoinPoint point) throws Throwable {
        log.info("{} ==> around before", className);
        Object result = point.proceed();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);
        RestController annotation1 = point.getTarget().getClass().getAnnotation(RestController.class);
        System.out.println(annotation1);
        String strTemplate = annotation.value();
        LogAnnotationSpElParse logAnnotationSpElParse = mapper.get(strTemplate);
        if (logAnnotationSpElParse == null) {
            logAnnotationSpElParse = new LogAnnotationSpElParse(strTemplate);
            mapper.put(strTemplate, logAnnotationSpElParse);
        }
        //设置解析上下文(有哪些占位符，以及每种占位符的值)
        EvaluationContext context = new StandardEvaluationContext();
        //获取参数值
        Object[] args = point.getArgs();
        //获取运行时参数的名称
        DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);
        if (parameterNames != null) {
            int size = Math.min(args.length, parameterNames.length);
            for (int i = 0; i < size; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        context.setVariable("result", result);
        // 解析,获取替换后的结果
        String data = logAnnotationSpElParse.getValue(strTemplate, context);
        System.out.println(data);
        log.info("{} ==> around after", className);
        return result;
    }

    @AfterThrowing(pointcut = "controller()", throwing = "e")
    public void doAfterThrowing(JoinPoint point, Throwable e) {
        log.error("log error:{}", e.getMessage());
    }

}