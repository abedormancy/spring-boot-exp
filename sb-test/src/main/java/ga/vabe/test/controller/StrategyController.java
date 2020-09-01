package ga.vabe.test.controller;

import ga.vabe.test.service.CalculateOperationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author laoliangliang
 * @date 2019/10/28 10:52
 */
@RestController
public class StrategyController {

    @Autowired
    private CalculateOperationContext calculateOperationContext;

    @RequestMapping(value = "/operation/{a}/{b}")
    public String strategySelect(@RequestParam("mode") int mode, @PathVariable int a, @PathVariable int b) {
        return String.valueOf(calculateOperationContext.get(mode)
                .orElseThrow(() -> new IllegalArgumentException("错误的策略"))
                .doOperation(a, b));
    }

}