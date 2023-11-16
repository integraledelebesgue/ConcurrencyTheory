import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
    public final int length;
    private final int[] data;
    private final Lock lock = new ReentrantLock();

    public Buffer(int length, int start) {
        this.length = length;
        this.data = new int[length];
        fillWIth(start);
    }

    private void fillWIth(int start) {
        for (int i = 0; i < length; i++)
            data[i] = start;
    }

    public Condition newCondition() {
        return lock.newCondition();
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public void put(int index, int object) {
        data[index] = object;
    }

    public int get(int index) {
        return data[index];
    }

    public void log() {
        System.out.println("Buffer: " + this);
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}
