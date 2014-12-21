package task;


public class ParallelHandler {
    final Object lock = new Object();
    StringBuilder line;
    int length;
    int resultBalance = 0;

    ParallelHandler(StringBuilder line) {
        this.line = line;
        this.length = line.length();
    }

    /*
     * Deals with first half of string.
     */
    class ForwardHandler implements Runnable {
        int midBalance = 0;
        int minBalance = 0;
        int min = 0;
        final Object midLock = new Object();
        @Override
        public void run() {
            Thread thread1 = new Thread(()-> {
                int counter = 0;
                int endIndex = length/4;
                for (int i = 0; i < endIndex; i++) {
                    if (line.charAt(i) == '(') {
                        counter++;
                    } else {
                        counter--;
                    }
                    if (counter < 0)
                        throw new IllegalStateException("Incorrect");
                }
                synchronized (midLock) {
                    minBalance += counter;
                    midBalance += counter;
                }
            });
            Thread thread2 = new Thread(()-> {
                int counter = 0;
                int startIndex = length/4;
                int endIndex = length/2;
                for (int i = startIndex; i < endIndex; i++) {
                    if (line.charAt(i) == '(') {
                        counter++;
                    } else {
                        counter--;
                    }
                    if (counter < min) {
                        min = counter;
                    }
                }

                synchronized (midLock) {
                    minBalance += min;
                    midBalance += counter;
                }
            });

            try {
                thread1.start();
                thread2.start();

                thread1.join();
                thread2.join();

                if (midBalance < 0 || minBalance < 0) {
                    throw new IllegalStateException("Incorrect");
                }
                synchronized (lock) {
                    resultBalance += midBalance;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * Deals with first half of reversed string
     */
    class ReversedHandler implements Runnable {
        int midBalance = 0;
        int minBalance = 0;
        int min = 0;
        final Object midLock = new Object();
        @Override
        public void run() {
            Thread thread1 = new Thread(()-> {
                int counter = 0;
                int startIndex = length - 1;
                int endIndex = length *3/4;
                for (int i = startIndex; i > endIndex; i--) {
                    if (line.charAt(i) == '(') {
                        counter--;
                    } else {
                        counter++;
                    }
                    if (counter < 0)
                        throw new IllegalStateException("Incorrect");
                }
                synchronized (midLock) {
                    minBalance += counter;
                    midBalance += counter;
                }
            });
            Thread thread2 = new Thread(()-> {
                int counter = 0;
                int startIndex = length *3/4;
                int endIndex = length/2;
                for (int i = startIndex; i >= endIndex; i--) {
                    if (line.charAt(i) == '(') {
                        counter--;
                    } else {
                        counter++;
                    }
                    if (counter < min) {
                        min = counter;
                    }
                }
                synchronized (midLock) {
                    minBalance += min;
                    midBalance += counter;
                }
            });

            try {
                thread1.start();
                thread2.start();

                thread1.join();
                thread2.join();
                if (midBalance < 0 || minBalance < 0) {
                    throw new IllegalStateException("Incorrect");
                }
                synchronized (lock) {
                    resultBalance -= midBalance;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    public long runParallel() throws InterruptedException {
        long startTime = System.nanoTime();

        Thread t1 = new Thread(new ForwardHandler());
        Thread t2 = new Thread(new ReversedHandler());

        try {
            t1.start();
            t2.start();

            t1.join();
            t2.join();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        long endTime = System.nanoTime();
        long resultTime = endTime - startTime;
        if (resultBalance == 0) {
            System.out.println("Correct");
        } else {
            System.out.println("Wrong");
        }
        System.out.println("Parallel method takes " + resultTime + " nanosec");
        return resultTime;
    }
}
