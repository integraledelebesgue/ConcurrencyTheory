public class Main {
    private static final int numActions = 100000;

    public static void main(String[] args) throws InterruptedException {
        BinarySemaphore semaphore = new BinarySemaphore();
        Counter counter = new Counter();

        Thread incrementer = new Thread(() -> {
            for (int i = 0; i < numActions; i++)
                incrementerAction(semaphore, counter);
        });

        Thread decrementer = new Thread(() -> {
            for (int i = 0; i < numActions; i++)
                decrementerAction(semaphore, counter);
        });

        incrementer.start();
        decrementer.start();

        incrementer.join();
        decrementer.join();

        System.out.println(counter);
    }

    private static void incrementerAction(BinarySemaphore sem, Counter counter) {
        try {
            sem.acquire();
            counter.increment();
            sem.release();
        } catch (Exception ignore) {}
    }

    private static void decrementerAction(BinarySemaphore semaphore, Counter counter) {
        try {
            semaphore.acquire();
            counter.decrement();
            semaphore.release();
        } catch (Exception ignore) {}
    }
}