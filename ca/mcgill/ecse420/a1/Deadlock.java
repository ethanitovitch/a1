package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Deadlock {

    private static ReentrantLock lock1 = new ReentrantLock();
    private static ReentrantLock lock2 = new ReentrantLock();

    public static void main(String[] args) {
        // Create a thread pool with two threads
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new Thread1());
        executor.execute(new Thread2());
        executor.shutdown();
    }

    // A task for adding an amount to the account
    public static class Thread1 implements Runnable {
        public void run() {
            synchronized (lock1){
                System.out.println(Thread.currentThread().getName() + " locked reource 1");

                // Wait so that thread2 can acquire lock2
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Thread 1 Should never gain access because it must wait
                // for Thread 2 to give up lock 2 causing deadlock
                synchronized (lock2){
                    System.out.println(Thread.currentThread().getName() + " locked reource 2");
                }
            }
        }
    }

    // A task for subtracting an amount from the account
    public static class Thread2 implements Runnable {
        public void run() {
            synchronized (lock2){
                System.out.println(Thread.currentThread().getName() + " locked reource 2");

                // Thread 2 Should never gain access because it must wait
                // for Thread 1 to give up lock 1 causing deadlock
                synchronized (lock1){
                    System.out.println(Thread.currentThread().getName() + " locked reource 1");
                }
            }
        }
    }
}