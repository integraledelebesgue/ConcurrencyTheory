import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;

public class Main {
    private static final int numThreads = 4;
    private static final int bufferLength = 6;
    private static final int maxWorkerActions = 12;

    public static void main(String[] args) {
        var buffer = new Buffer(bufferLength, -1);
        buffer.log();

        try (ExecutorService threadPool = Executors.newFixedThreadPool(numThreads)) {
            spawnAll(threadPool, buffer);
        }
    }

    private static void spawnAll(ExecutorService threadPool, Buffer buffer) {
        var producerPredecessor = buffer.newCondition();
        var successor = buffer.newCondition();

        spawnWorker(threadPool, buffer, producerPredecessor, successor, -1, 0);

        for (int i = 0; i < numThreads - 2; i++) {
            var predecessor = successor;
            successor = buffer.newCondition();
            spawnWorker(threadPool, buffer, predecessor, successor, i, i + 1);
        }

        spawnWorker(threadPool, buffer, successor, producerPredecessor, numThreads - 2, -1);
    }

    private static void spawnWorker(ExecutorService threadPool, Buffer buffer, Condition predecessor, Condition successor, int input, int output) {
        threadPool.execute(new Worker(buffer, input, output, predecessor, successor, maxWorkerActions));
    }
}