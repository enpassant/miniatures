package threads;

import java.lang.Runtime;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Threads {
    private static int platformCount = 0;

    private final ExecutorService blocking = Executors.newCachedThreadPool(
        new PlatformThreadFactory("io-blocking")
    );
    private final ExecutorService executor = Executors.newFixedThreadPool(
        java.lang.Runtime.getRuntime().availableProcessors(),
        new PlatformThreadFactory("io-executor")
    );
    private final ExecutorService forkJoin = new ForkJoinPool(1);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
        java.lang.Runtime.getRuntime().availableProcessors(),
        new PlatformThreadFactory("io-scheduler")
    );

    public Threads() {
        platformCount++;
    }

    public void shutdown() {
        blocking.shutdown();
        executor.shutdown();
        forkJoin.shutdown();
        scheduler.shutdown();
    }

    public ExecutorService getBlocking() {
        return blocking;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public ExecutorService getForkJoin() {
        return forkJoin;
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    };

    class PlatformThreadFactory implements ThreadFactory {
        private final String poolName;
        private int threadCount = 0;

        public PlatformThreadFactory(String poolName) {
            this.poolName = poolName;
        }

        public Thread newThread(Runnable r) {
            threadCount++;
            return new Thread(r, poolName + "-" + platformCount + "-thread-" + threadCount);
        }
    }

    private static long measureTime(Runnable task) {
        final long start = System.nanoTime();

        task.run();

        return (System.nanoTime() - start) / 1000000L;
    }

    private static void blockingIO(long millis) {
        try {
            Thread.sleep(millis);
        } catch(Exception e) {
        }
    }

    private static BigDecimal fib(BigDecimal a, BigDecimal b, long index) {
        if (index <= 1) return a;
        else if (index == 2) return b;
        else return fib(b, a.add(b), index - 1);
    }

    public static BigDecimal fibonacci(long index) {
        return fib(BigDecimal.ONE, BigDecimal.ONE, index);
    }

    public static void calcFibonacci(long count) {
        for (int i=0; i<count; i++) {
            fibonacci(46);
            Thread.yield();
        }
    }

    public static <T> Optional<T> getFuture(Future<T> f) {
        try {
            return Optional.of(f.get());
        } catch(Exception e) {
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("Available processors: " + availableProcessors);
        long cycleCount = 2000000L;

        Threads threads = new Threads();

        final long blockTime = measureTime(() ->
            blockingIO(500)
        );
        System.out.println("Blocking time: " + blockTime + " ms");

        final long calcTime = measureTime(() ->
            calcFibonacci(cycleCount)
        );
        System.out.println("Fibonacci time: " + calcTime + " ms");

        final long calcTimeAll = measureTime(() -> {
            for (int i=0; i<availableProcessors; i++) {
                calcFibonacci(cycleCount);
            }
        });
        System.out.println("All fibonacci time: " + calcTimeAll + " ms");

        final ExecutorService blocking = threads.getBlocking();

        final long calcTimeParallelProc = measureTime(() -> {
            IntStream.range(0, availableProcessors * 100)
                .parallel()
                .mapToObj(i -> blocking.submit(
                    () -> calcFibonacci(cycleCount / 100)
                ))
                .map(Threads::getFuture)
                .collect(Collectors.toList());
        });
        System.out.println("Parallel proc. fibonacci time: " + calcTimeParallelProc + " ms");

        threads.shutdown();
    }
}
