package ga.vabe.mybatis.common;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.LongAdder;

@Component
public class AppContext {

    // 最小返回时间（超过并发边界时起作用，用于降低接口响应速度控制压测时cpu占用
    public long returnMillis = 60L;
    // 并发边界（超过多少并发时开启数据延迟批量插入和接口最小返回时间
    public long guestBound = 0;
    // 用于统计当前统计的接口并发数
    private final LongAdder guest = new LongAdder();

    public void guestIncrement() {
        guest.increment();
    }

    public void guestDecrement() {
        guest.decrement();
    }

    public int guest() {
        return guest.intValue();
    }

    public boolean overGuest() {
        return guest() > guestBound;
    }
}
