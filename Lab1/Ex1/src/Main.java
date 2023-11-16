public class Main {
    private static final int actionCount = 100000000;

    public static void main(String[] args) throws Exception {
        Counter counter = new Counter();

        Thread incrementer = new Thread(() -> {
            for (int i = 0; i < actionCount; i++)
                counter.increment();
        });

        Thread decrementer = new Thread(() -> {
            for (int i = 0; i < actionCount; i++)
                counter.decrement();
        });

        incrementer.start();
        decrementer.start();

        incrementer.join();
        decrementer.join();

        System.out.println(counter);
    }
}