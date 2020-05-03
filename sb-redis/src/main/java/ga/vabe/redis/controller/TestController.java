package ga.vabe.redis.controller;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final LongAdder adder = new LongAdder();

    private static int loop = 0;

    private static int delay = 0;

    private static RateLimiter limiter = null;

    @RequestMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String index() {
        return "loop: " + loop + ", delay: " + delay + ", moment: " + adder.intValue();
    }


    @RequestMapping(value = "/loop/{count}/{delay}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String setLoop(@PathVariable int count, @PathVariable int delay) {
        loop = count;
        TestController.delay = delay;
        return "loop: " + loop + ", delay: " + delay;
    }

    @RequestMapping(value = "/limiter/{size}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String setLimiter(@PathVariable int size) {
        limiter = RateLimiter.create(size);
        return "limiter: " + size;
    }

    /**
     * 正常无限制接口
     */
    @RequestMapping(value = "/normal", produces = MediaType.TEXT_PLAIN_VALUE)
    public String normal() {
        long begin = System.currentTimeMillis();
        adder.increment();
        loop(loop);
        long elapse = System.currentTimeMillis() - begin;
        String result = "moment: " + adder.intValue() + "\n" + "elapse: " + elapse + "\n" + "actual: " + elapse;
        adder.decrement();
        return "ok";
    }

    /**
     * sleep
     */
    @RequestMapping(value = {"/sleep/{millis}", "/sleep"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public String sleep(@PathVariable(required = false) Long millis) {
        long begin = System.currentTimeMillis();
        adder.increment();
        loop(loop);
        long elapse = System.currentTimeMillis() - begin;
        int moment = adder.intValue();
        long actual = 0L;
        if (moment > 2 && millis != null) {
            actual = millis - elapse;
            delay(actual);
        }

        String result = "moment: " + moment + "\n" + "elapse: " + elapse + "\n" + "actual: " + actual;
        adder.decrement();
        return "ok";
    }

    /**
     * rate limiter 限流
     */
    @RequestMapping(value = "/limiter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String limiter() {
        if (limiter == null) {
            return "please setting rate limiter size";
        }
        long begin = System.currentTimeMillis();
        adder.increment();
        double acquire = limiter.acquire(1);
        loop(loop);
        long elapse = System.currentTimeMillis() - begin;
        String result = "moment: " + adder.intValue() + "\n" + "elapse: " + elapse + "\n" + "actual: " + elapse;
        adder.decrement();
        return "ok";
    }

    private void delay(long millis) {
        if (millis < 1L) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    long loop(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        long result = 0L;
        for (int i = 0; i < amount; i++) {
            if (i % 10000 == 0) {
                delay(delay);
            }
            result += ThreadLocalRandom.current().nextInt(1, 100);
        }
        return result;
    }

}
