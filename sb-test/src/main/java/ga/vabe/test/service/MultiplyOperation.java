package ga.vabe.test.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1000)
public class MultiplyOperation implements CalculateStrategy {
    @Override
    public int doOperation(int num1, int num2) {
        return num1 * num2;
    }

    @Override
    public String desc() {
        return " * ";
    }

}