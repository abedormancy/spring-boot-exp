package ga.vabe.redis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {


    public static void main(String[] args) {
        TestController t = new TestController();
        if (t instanceof Object) {
            System.out.println("okok");
        }
    }

}
