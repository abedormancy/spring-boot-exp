package ga.vabe.log.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@Slf4j
public class IndexController {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * 日志级别
     * trace < debug < info < warn < error
     * @return
     */
    @RequestMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String index() {
        return "hello world";
    }

    /**
     * 很低的日志级别，一般不使用
     * @return
     */
    @RequestMapping(value = "/trace")
    public String trace() {
        String info = "trace: " + LocalDateTime.now().format(DATE_TIME_FORMAT);
        log.trace("warn: {}", Thread.currentThread().getName());
        return info;
    }

    /**
     * 一般放于程序的某个关键点，用于打印变量值或返回信息等
     * @return
     */
    @RequestMapping(value = "/debug")
    public String debug() {
        String info = "debug: " + LocalDateTime.now().format(DATE_TIME_FORMAT);
        log.debug("debug: {}", Thread.currentThread().getName());
        return info;
    }

    /**
     * 一般在处理业务逻辑时用
     * @return
     */
    @RequestMapping(value = "/info")
    public String info() {
        String info = "info: " + LocalDateTime.now().format(DATE_TIME_FORMAT);
        log.info("info: {}", Thread.currentThread().getName());
        return info;
    }

    /**
     * 警告，不会影响程序正常运行，但值得注意
     * @return
     */
    @RequestMapping(value = "/warn")
    public String warn() {
        String info = "warn: " + LocalDateTime.now().format(DATE_TIME_FORMAT);
        log.warn("warn: {}", Thread.currentThread().getName());
        return info;
    }

    /**
     * 用于程序报错，必须解决的时候使用此级别打印日志
     * @return
     */
    @RequestMapping(value = "/errors")
    public String error() {
        String info = "error: " + LocalDateTime.now().format(DATE_TIME_FORMAT);
        log.error("error: {}", Thread.currentThread().getName());
        return info;
    }

}
