import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int seed = 0;
    private static final Random generator = new Random(seed);
    private static final int numPrinters = 5;
    private static final int numThreads = 10;
    private static final int numActions = 3;

    public static void main(String[] args) {
        PrinterMonitor monitor = new PrinterMonitor(numPrinters);

        try (
                ExecutorService threadPool = Executors.newFixedThreadPool(numThreads)
        ) {
            for (int i = 0; i < numThreads; i++)
                threadPool.execute(() -> {
                    try {
                        action(monitor);
                    } catch (Exception ignore) {}
                });
        }
    }

    private static void action(PrinterMonitor monitor) throws InterruptedException {
        int printerNumber;

        for (int i = 0; i < numActions; i++) {
            printerNumber = monitor.acquire();

            String lockMessage = String.format(
                "Thread %d uses printer no. %d",
                Thread.currentThread().threadId(),
                printerNumber
            );

            String releaseMessage = String.format(
                "Thread %d releases printer no. %d",
                Thread.currentThread().threadId(),
                printerNumber
            );

            System.out.println(lockMessage);

            int time = 500 + generator.nextInt() % 500;
            Thread.sleep(time);

            System.out.println(releaseMessage);

            monitor.release(printerNumber);
        }
    }
}