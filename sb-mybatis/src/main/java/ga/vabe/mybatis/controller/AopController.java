package ga.vabe.mybatis.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Aop 实现接口延迟返回
 */
@Slf4j
@RestController
@RequestMapping("/aop")
public class AopController {

    @RequestMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String index() {
        return "aop ok";
    }

    @RequestMapping(value = "/add", produces = MediaType.TEXT_PLAIN_VALUE)
    public String addWithDelay() {
        return "aop add";
    }

}
