public class Counter {
    private volatile int state = 0;

    public void increment() {
        state += 1;
    }

    public void decrement() {
        state -= 1;
    }

    @Override
    public String toString() {
        return String.valueOf(state);
    }
}
