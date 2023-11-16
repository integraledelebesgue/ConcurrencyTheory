package main;

import java.util.ArrayList;

public class Main {
    static final int threadCount = 1000;
    static final int loopCount = 1000;

    public static void main(String args[]) throws Exception {
        Counter counter = new Counter();
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            Incrementer inc = new Incrementer(counter, loopCount);
            Decrementer dec = new Decrementer(counter, loopCount);

            threads.add(inc);
            threads.add(dec);

            inc.start();
            dec.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println(counter);
    }

    public static class Counter {
        private int state = 0;

        public void increment() {
            state += 1;
        }

        public void decrement() {
            state -= 1;
        }

        public String toString() {
            return "" + state;
        }
    }

    public static class Incrementer extends Thread {
        private Counter counter;
        private int operationsCount;

        public Incrementer(Counter counter, int operationsCount) {
            this.counter = counter;
            this.operationsCount = operationsCount;
        }

        public void run() {
            for (int i = 0; i < operationsCount; i++) {
                counter.increment();
            }
        }
    }

    public static  class Decrementer extends Thread {
        private Counter counter;
        private int operationsCount;

        public Decrementer(Counter counter, int operationsCount) {
            this.counter = counter;
            this.operationsCount = operationsCount;
        }

        public void run() {
            for (int i = 0; i < operationsCount; i++) {
                counter.decrement();
            }
        }
    }
}