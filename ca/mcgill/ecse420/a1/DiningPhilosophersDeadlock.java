package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiningPhilosophersDeadlock {

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
				Thread.sleep((long) (Math.random() * 10));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
		executor.shutdown();
	}

	public static class Philosopher implements Runnable {

		private Object leftChopstick;
		private Object rightChopstick;

		public Philosopher(Object[] chopsticks, int position, int numberOfPhilosophers) {
			this.leftChopstick = chopsticks[position];
			this.rightChopstick = chopsticks[(position + 1) % numberOfPhilosophers];
		}

		@Override
		public void run() {
			while (true) {
				try {
					// Wait until left chopstick is available
					synchronized (leftChopstick) {
						// Pick up left chopstick
						System.out.println(Thread.currentThread().getName() + ": Picked Up Left Chopstick");
						Thread.sleep((long) (Math.random() * 10));

						// Wait until right chopstick is available
						synchronized (rightChopstick) {
							// Pick up right chopstick and eat
							System.out.println(Thread.currentThread().getName() + ": Picked Up Right Chopstick/Eating");
							Thread.sleep((long) (Math.random() * 10));
						}
						// Put down right chopstick and eat
						System.out.println(Thread.currentThread().getName() + ": Put Down Right Chopstick");
					}
					// Put down left chopstick and eat
					System.out.println(Thread.currentThread().getName() + ": Put Down Left Chopstick");
					Thread.sleep((long) (Math.random() * 10));

				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		}
	}
}