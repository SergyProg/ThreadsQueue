package ru.netology;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CallCenterExample {
    static final int NUMBER_OF_CALLS = 60;
    static final int RESPONSE_TIME = 3000;
    static final int INTERVAL_BETWEEN_CALLS = 1000;
    static final int NUMBER_OF_SPECIALIST = 5;

    static final String DONE = "End";

    private BlockingQueue<String> drop;
    volatile boolean stopThreads = false;

    public CallCenterExample() {
        drop = new ArrayBlockingQueue<String>(NUMBER_OF_SPECIALIST, true);
        (new Thread(new Calls())).start();
        ThreadGroup specialistsGroup = new ThreadGroup("Specialists");
        for (int i = 1; i <= NUMBER_OF_SPECIALIST; i++)
            (new Thread(specialistsGroup, new Specialists())).start();
    }

    class Calls implements Runnable {
        public void run() {
            try {
                for (int i = 1; i <= NUMBER_OF_CALLS; i++) {
                    drop.put("звонок " + i);
                    System.out.println("На АТС поступил звонок " + i);
                    Thread.sleep(INTERVAL_BETWEEN_CALLS);
                }
                drop.put(DONE);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    class Specialists implements Runnable {
        public void run() {
            String call = null;
            try {
                 while (true && !stopThreads) {
                        call = drop.take();
                        if (call ==  DONE) {
                            stopThreads = true;
                            break;
                        }
                        System.out.println("Специалист (" + Thread.currentThread().getName() + ") ответил на " + call);
                        Thread.sleep(RESPONSE_TIME);
                }
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new CallCenterExample();
    }
}
