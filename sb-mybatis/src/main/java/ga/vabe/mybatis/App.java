package ga.vabe.mybatis;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validator;
import lombok.Data;
import lombok.ToString;

public class App {

    public static void main(String[] args) {
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

@Data
@ToString
class Person {

    private String name;

    private int age;

}