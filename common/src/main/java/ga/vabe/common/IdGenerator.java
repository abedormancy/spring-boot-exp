package ga.vabe.common;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <b>description:</b><br/>
 * Lite SnowFlake
 * 0-  0000000000 0000000000 0000000000 0000000000 00 - 00000 - 0000000000000000
 * 可使用139年 , ( (1 << 42) / (3600 * 24 * 365 * 1000) ~= 139 )
 * 42 bit timestamp (存储的是timestamp的差值)
 * 12 bit sequence (每毫秒最多可生成 4,096个id)
 * 9 bit random ( 512 ) 用于提高 id 猜测难度
 * @author Abe
 * @date 0/24/2018
 */
public class IdGenerator {

    /**
     * 起始 timestamp
     */
    private static final long START_STAMP = 636600000000L;

    /**
     * 随机位
     */
    private static final long RANDOM_BIT = 9L;

    /**
     * 序列位
     */
    private static final long SEQUENCE_BIT = 12L;

    /**
     * 序列位左移
     */
    private static final long SEQUENCE_LEFT = RANDOM_BIT;

    /**
     * 时间戳差左移位
     */
    private static final long STAMP_LEFT = SEQUENCE_BIT + RANDOM_BIT;

    /**
     * 生成序列的掩码
     */
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 序列的开始
     */
    private long sequence = 0L;

    /**
     * 上次生成的 id 时间戳
     */
    private long lastTimestamp = -1L;

    private final static char[] DIGITS = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'm', 'n', 'p', 'q',
            'r', 's', 'u', 'u', 'v', 'w',
            'x', 'y'
    };

    private static final IdGenerator ID_GENERATOR = new IdGenerator();

    private IdGenerator() {}

    public static final IdGenerator instance() {
        return ID_GENERATOR;
    }

    synchronized public final long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d seconds",
                    lastTimestamp - timestamp));
        } else if (timestamp == lastTimestamp) {
            // 同一秒内生成的id, 序列加1
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = nextTime();
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return (timestamp - START_STAMP) << STAMP_LEFT |
                sequence << SEQUENCE_LEFT |
                ThreadLocalRandom.current().nextLong(0L, 1L << RANDOM_BIT);
    }

    private final long nextTime() {
        long now = -1;
        while (now <= lastTimestamp) {
            now = System.currentTimeMillis();
        }
        return now;
    }

    public final String hexId() {
    	return toUnsignedString(nextId(), 4);
    }

    private static String toUnsignedString(long i, int shift) {
        char[] buf = new char[64];
        int charPos = 64;
        long mask = -1 ^ (-1 << shift);
        do {
            buf[--charPos] = DIGITS[(int) (i & mask)];
            i >>>= shift;
        } while (i != 0);
        return new String(buf, charPos, (64 - charPos));
    }

    public static void main(String[] args) {
        IdGenerator idGenerator = instance();
        for (int i = 0; i < 8; i++) {
            long id = idGenerator.nextId();
            System.out.println(id + " / " + toUnsignedString(id, 4) + " / " + toUnsignedString(id, 5));
        }
    }


}