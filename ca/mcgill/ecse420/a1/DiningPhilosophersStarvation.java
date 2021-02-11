package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiningPhilosophersStarvation {

    public static void main(String[] args) {

        int numberOfPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
        Object[] chopsticks = new Object[numberOfPhilosophers];
        ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

        // Initialize chopsticks
        for (int i = 0; i < numberOfPhilosophers; i++) {
            chopsticks[i] = new Object();
        }

        // Initialize Philosophers and call them
        for (int i = 0; i < numberOfPhilosophers; i++) {
            philosophers[i] = new Philosopher(chopsticks, i, numberOfPhilosophers);
            executor.execute((philosophers[i]));
            try {
                Thread.sleep((long) (Math.random() * 5));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        executor.shutdown();
    }

    public static class Philosopher implements Runnable {

        final int philosopher_id;
        private Object leftChopstick;
        private Object rightChopstick;

        Philosopher(Object[] chopsticks, int position, int numberOfPhilosophers) {
            this.philosopher_id = position + 1;

            // First philosopher will pick up the chopsticks in
            // the reverse order as the rest breaking the circular
            // dependency
            if (position == 0) {
                this.leftChopstick = chopsticks[(position + 1) % numberOfPhilosophers];
                this.rightChopstick = chopsticks[position];
            } else {
                this.leftChopstick = chopsticks[position];
                this.rightChopstick = chopsticks[(position + 1) % numberOfPhilosophers];
            }
        }

        @Override
        public void run() {
            long startTime;
            long waitTime = 0;

            for (int x = 0; x < 1000; x++) {
                startTime = System.nanoTime();
                try {
                    // Wait until left chopstick is available
                    synchronized (leftChopstick) {
                        // Pick up left chopstick
                        Thread.sleep((long) (Math.random() * 5));

                        // Wait until right chopstick is available
                        synchronized (rightChopstick) {
                            // Pick up right chopstick and eat
                            waitTime += System.nanoTime() - startTime;
                            Thread.sleep((long) (Math.random() * 5));
                        }
                        // Put down right chopstick and eat
                    }
                    // Put down left chopstick and eat
                    Thread.sleep((long) (Math.random() * 5));

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println("Philosopher " + philosopher_id + ": waited " + waitTime/1000000000.0 + " seconds");
        }
    }
}