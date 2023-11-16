import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrinterMonitor {
    private final Lock lock = new ReentrantLock();
    private final Condition waiting = lock.newCondition();
    private final Stack<Integer> free;

    public PrinterMonitor(int numPrinters) {
        free = IntStream
            .rangeClosed(1, numPrinters)
            .boxed()
            .collect(Collectors.toCollection(Stack::new));
    }

    public int acquire() throws InterruptedException {
        try {
            lock.lock();

            while (free.isEmpty())
                waiting.await();

            return free.pop();
        } finally {
            lock.unlock();
        }
    }

    public void release(int number) {
        try {
            lock.lock();
            free.push(number);
            waiting.signal();
        } finally {
            lock.unlock();
        }
    }
}
