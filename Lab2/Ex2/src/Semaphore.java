public class Semaphore {
    private volatile int state; // volatile because why not ✨

    public Semaphore(int initialState) throws IllegalArgumentException {
        if (initialState <= 0)
            throw new IllegalArgumentException("Initial state must be positive");

        state = initialState;
    }

    public synchronized void acquire() throws InterruptedException {
        while (state <= 0)
            wait();

        state -= 1;
    }

    public synchronized void release() {
        state += 1;
        notify();
    }
}
