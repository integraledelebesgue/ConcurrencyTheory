import java.util.concurrent.locks.Condition;

public class Worker extends Thread {
    private final Buffer buffer;
    private final int input;
    private final int output;
    private int state;
    private final int maxActions;
    private final Condition successor;
    private final Condition predecessor;

    public Worker(Buffer buffer, int input, int output, Condition predecessor, Condition successor, int maxActions) {
        this.buffer = buffer;
        this.input = input;
        this.output = output;
        this.state = 0;
        this.maxActions = maxActions;
        this.successor = successor;
        this.predecessor = predecessor;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < maxActions; i++)
                action();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void action() throws InterruptedException {
        buffer.lock();

        while (buffer.get(state) != input)
            predecessor.await();

        buffer.put(state, output);
        buffer.log();

        successor.signal();

        state += 1;
        state %= buffer.length;

        buffer.unlock();
    }

}
