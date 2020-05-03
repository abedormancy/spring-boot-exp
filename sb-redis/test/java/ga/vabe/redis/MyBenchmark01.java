package ga.vabe.redis;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
// @Threads(10)
@Fork(1)
@Measurement(iterations = 2, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 800, timeUnit = TimeUnit.MILLISECONDS) // 800ms 预热
// 参数解释 https://blog.csdn.net/singgel/article/details/105412882
// 官方示例 http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
public class MyBenchmark01 {

    public static final Map<String, String> CACHE = new HashMap<String, String>() {{
        put("1", "a");
        put("2", "b");
        put("3", "c");
        put("4", "d");
        put("5", "e");
        put("6", "f");
    }};

    /**
     * MyBenchmark01.test01  thrpt    2  25806810.544          ops/s
     */
    @Benchmark
    public String test01() {
        int value = ThreadLocalRandom.current().nextInt(1, 7);
        return CACHE.get(String.valueOf(value));
    }

    /**
     * MyBenchmark01.test02  thrpt    2  20843841.078          ops/s
     */
    @Benchmark
    public String test02() {
        String value = String.valueOf(ThreadLocalRandom.current().nextInt(1, 7));
        if ("1".equals(value)) {
            return "a";
        } else if ("2".equals(value)) {
            return "b";
        } else if ("3".equals(value)) {
            return "c";
        } else if ("4".equals(value)) {
            return "d";
        } else if ("5".equals(value)) {
            return "e";
        } else if ("6".equals(value)) {
            return "f";
        }
        return null;
    }

    /**
     * MyBenchmark01.test03  thrpt    2  21119578.128          ops/s
     */
    @Benchmark
    public String test03() {
        String value = String.valueOf(ThreadLocalRandom.current().nextInt(1, 7));
        switch (value) {
            case "1":
                return "a";
            case "2":
                return "b";
            case "3":
                return "c";
            case "4":
                return "d";
            case "5":
                return "e";
            case "6":
                return "f";
            default:
                return null;
        }
    }

    /**
     * 1. 代码调用方式；
     * ps: 通过安装 jmh plugin 插件后可以类似 junit 执行测试，无需入口
     *
     * @param args
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        // 通过注解可代替如下显示参数配置
        Options opt = new OptionsBuilder()
                .include(MyBenchmark01.class.getSimpleName())
                .warmupIterations(1) // 预热循环次数
                .warmupTime(TimeValue.milliseconds(100L)) // 预热时间
                .measurementIterations(2) // 测试循环次数
                .measurementTime(TimeValue.milliseconds(1000L)) // 循环时间
                .build();
        new Runner(opt).run();
    }


}