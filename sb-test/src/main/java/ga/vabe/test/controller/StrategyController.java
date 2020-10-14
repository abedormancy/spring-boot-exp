package ga.vabe.test.controller;

import ga.vabe.other.OtherPkgClass;
import ga.vabe.test.service.CalculateOperationContext;
import ga.vabe.test.service.CalculateStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class StrategyController {

    @Autowired
    private CalculateOperationContext calculateOperationContext;

    @Autowired(required = false)
    private OtherPkgClass otherPkgClass;

    @Autowired
    private CalculateStrategy addOperation;

    @RequestMapping
    public String index(HttpServletRequest request) {
        return "over.hell > " + otherPkgClass;
    }

    @RequestMapping(value = "/operation/{a}/{b}")
    public String strategySelect(@RequestParam("mode") int mode, @PathVariable int a, @PathVariable int b) {
        return String.valueOf(calculateOperationContext.get(mode)
                .orElseThrow(() -> new IllegalArgumentException("错误的策略"))
                .doOperation(a, b));
    }

    @RequestMapping(value = "/add/{a}/{b}")
    public String add(@PathVariable int a, @PathVariable int b) {
        return a + addOperation.desc() + b + " = " + addOperation.doOperation(a, b);
    }
}