package ga.vabe.test.annotation;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    String value() default "";

}
