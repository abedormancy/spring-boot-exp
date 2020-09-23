package ga.vabe.redis;

import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@BenchmarkMode(Mode.Throughput)
@Fork(1)
@Measurement(iterations = 2, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 800, timeUnit = TimeUnit.MILLISECONDS)
public class MyBenchmark02 {


    /**
     * MyBenchmark02.initArray01  thrpt    2  342903947.879          ops/s
     */
    @Benchmark
    public int initArray01() {
        int[] array = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        return array.length;
    }

    /**
     * MyBenchmark02.initArray02  thrpt    2  340074182.682          ops/s
     */
    @Benchmark
    public int initArray02() {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        pool.execute(() -> {
            System.out.println("benchmark");
        });
        int[] array = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        return array.length;
    }

    /**
     * MyBenchmark02.initArray03  thrpt    2  339521081.722          ops/s
     */
    @Benchmark
    public int initArray03() {
        int[] array = new int[10];
        return array.length;
    }


    /**
     * MyBenchmark02.listFile01  avgt    2  0.383           s/op
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void listFile01() throws Exception {
        LongAdder count = new LongAdder();
        Files.walkFileTree(Paths.get("r:/"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                count.increment();
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     *
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void listFile02() throws Exception {
        LongAdder count = new LongAdder();
        Files.newDirectoryStream(Paths.get("r:/")).forEach(path -> count.increment());
    }

}