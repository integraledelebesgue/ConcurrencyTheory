public class BinarySemaphore {
    private boolean state = true;

    public synchronized void acquire() throws InterruptedException {
        while (!state)
            wait();

        state = false;
    }

    public synchronized void release() {
        state = true;
        notify();
    }
}
