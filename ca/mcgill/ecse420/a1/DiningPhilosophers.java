package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {

    public static void main(String[] args) {

        int numberOfPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
        Chopstick[] chopsticks = new Chopstick[numberOfPhilosophers];
        ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

        // Initialize chopsticks
        for (int i = 0; i < numberOfPhilosophers; i++) {
            chopsticks[i] = new Chopstick();
        }

        // Initialize Philosophers and call them
        for (int i = 0; i < numberOfPhilosophers; i++) {
            if (i == 0) {
                philosophers[i] = new Philosopher(i, chopsticks[chopsticks.length - 1], chopsticks[i]);
            } else {
                philosophers[i] = new Philosopher(i, chopsticks[i - 1], chopsticks[i]);
            }
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

    public static class Chopstick {

        // ReentrantLock with fair flag set to true. This avoids starvation
        // since it prioritizes threads which have been waiting for longer
        private final ReentrantLock reentrantLock = new ReentrantLock(true);

        public Chopstick() {}

        // Lock Chopstick on Pick up
        public boolean PickUp() {
            return reentrantLock.tryLock();
        }

        // Unlock Chopstick on put down
        public void PutDown() {
            reentrantLock.unlock();
        }
    }

    public static class Philosopher implements Runnable {

        int count = 0;
        final int philosopher_id;
        private Chopstick rightChopstick;
        private Chopstick leftChopstick;

        public Philosopher(int position, Chopstick rightChopstick, Chopstick leftChopstick) {
            this.philosopher_id = position + 1;
            this.leftChopstick = leftChopstick;
            this.rightChopstick = rightChopstick;
        }

        @Override
        public void run() {
            long startTime;
            long waitTime = 0;

            for (int x = 0; x < 1000; x++) {
                startTime = System.nanoTime();

                try {
                    // Wait until left chopstick is available
                    if (leftChopstick.PickUp()) {
                        // Pick up left chopstick
                        Thread.sleep((long) (Math.random() * 5));

                        // Wait until right chopstick is available
                        if (rightChopstick.PickUp()) {
                            // Pick up right chopstick and eat
                            waitTime += System.nanoTime() - startTime;
                            count++;
                            Thread.sleep((long) (Math.random() * 5));

                            // Put down right chopstick and eat
                            rightChopstick.PutDown();
                        }
                        // Put down left chopstick and eat
                        leftChopstick.PutDown();
                    }
                    Thread.sleep((long) (Math.random() * 5));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Philosopher " + philosopher_id + ": ate " + count + " times and waited " + waitTime / 1000000000.0 + " seconds");
        }
    }
}