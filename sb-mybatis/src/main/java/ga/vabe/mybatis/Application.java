package ga.vabe.mybatis;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validator;
import ga.vabe.mybatis.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@MapperScan(basePackages = "ga.vabe.mybatis.dao")
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     *  validator 测试
     */
    public static void test01() {
        Validator<Person> validator = ValidatorBuilder.<Person>of()
                .constraint(Person::getName, "name", c -> c.notBlank().lessThanOrEqual(4))
                .constraint(Person::getAge, "age", c -> c.greaterThan(0).lessThanOrEqual(120)).build();
        Person p = new Person();
        p.setAge(10);
        p.setName("abe1a");
        ConstraintViolations validate = validator.validate(p);
        validate.forEach(x -> {
            System.out.println(x.message());
            System.out.println(x.name());
            System.out.println(x.messageKey());
        });
    }

}
