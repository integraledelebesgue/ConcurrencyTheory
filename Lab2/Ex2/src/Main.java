import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int numThreads = 20;
    private static final int numPermits = 5;
    private static final int seed = 2137;
    private static final Random randomGen = new Random(seed);

    public static void main(String[] args) {
        Semaphore sem = new Semaphore(numPermits);

        try (
                ExecutorService threadPool = Executors.newFixedThreadPool(numThreads)
        ) {
            for (int i = 0; i < numThreads; i++)
                threadPool.execute(() -> {
                    try {
                        action(sem);
                    } catch (Exception ignore) {}
                });
        }
    }

    private static void randomSleep() throws InterruptedException {
        int time = 500 + randomGen.nextInt() % 500;
        Thread.sleep(time);
    }

    private static void logAcquire() {
        System.out.format("Thread %d acquired semaphore\n", Thread.currentThread().threadId());
    }

    private static void logRelease() {
        System.out.format("Thread %d released semaphore\n", Thread.currentThread().threadId());
    }

    private static void action(Semaphore sem) throws InterruptedException {
        sem.acquire();
        logAcquire();

        randomSleep();

        sem.release();
        logRelease();
    }
}