import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int producerCount = 10;
    private static final int producerActionCount = 5;

    private static final int consumerCount = 10;
    private static final int consumerActionCount = 5;

    public static void main(String[] args) {
        Buffer buffer = new Buffer();

        try (
                ExecutorService producers = Executors.newFixedThreadPool(producerCount);
                ExecutorService consumers = Executors.newFixedThreadPool(producerCount)
        ) {
            for (int i = 0; i < producerCount; i++)
                producers.execute(new Producer(producerActionCount, buffer));

            for (int i = 0; i < consumerCount; i++)
                consumers.execute(new Consumer(consumerActionCount, buffer));
        }
    }
}
