package ga.vabe.test.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class AddOperation implements CalculateStrategy {

    @Override
    public int doOperation(int num1, int num2) {
        return num1 + num2;
    }

    @Override
    public String desc() {
        return "相加";
    }

}