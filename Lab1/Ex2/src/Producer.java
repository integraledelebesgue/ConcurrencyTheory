public class Producer extends Thread {
    private final Buffer buffer;
    private final int count;

    private final int id;
    private static int instances = 0;

    public Producer(int count, Buffer buffer) {
        this.count = count;
        this.buffer = buffer;
        this.id = instances;
        instances += 1;
    }

    @Override
    public void run() {
        for (int i = 0; i < count; i++) {
            try {
                String message = String.format("Message %d.%d", id, i);
                buffer.put(message);
                System.out.format("Producer %d put %s from buffer\n", id, message);
            } catch (InterruptedException ignore) {}
        }
    }
}
