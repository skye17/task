package task;


public class ParallelHandler {
    final Object lock = new Object();
    StringBuilder line;
    int result = 0;

    ParallelHandler(StringBuilder line) {
        this.line = line;
    }
    // First 1/2 of string
    class Handler1 implements Runnable {
        int midResult = 0;
        final Object midLock = new Object();
        @Override
        public void run() {
            Thread thread1 = new Thread(()-> {
                int counter = 0;
                for (int i = 0; i < line.length() / 4; i++) {
                    if (line.charAt(i) == '(') {
                        counter++;
                    } else {
                        counter--;
                    }
                    if (counter < 0)
                        throw new IllegalStateException("Incorrect");
                }
                synchronized (midLock) {
                    midResult += counter;
                }
            });
            Thread thread2 = new Thread(()-> {
                int counter = 0;
                for (int i = line.length()/2 - 1; i >= line.length() / 4; i--) {
                    if (line.charAt(i) == '(') {
                        counter--;
                    } else {
                        counter++;
                    }
                }
                synchronized (midLock) {
                    midResult -= counter;
                }
            }, "thread2");

            try {
                thread1.start();
                thread2.start();

                thread1.join();
                thread2.join();

                if (midResult < 0) {
                    throw new IllegalStateException("Incorrect");
                }
                //System.out.println("Mid result for first 1/2 :" + midResult);
                synchronized (lock) {
                    result += midResult;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //First 1/2 of reversed string
    class Handler2 implements Runnable {
        int midResult = 0;
        final Object midLock = new Object();
        @Override
        public void run() {
            Thread thread1 = new Thread(()-> {
                int counter = 0;
                for (int i = line.length() - 1; i > line.length() * 3/ 4; i--) {
                    if (line.charAt(i) == '(') {
                        counter--;
                    } else {
                        counter++;
                    }
                    if (counter < 0)
                        throw new IllegalStateException("Incorrect");
                }
                synchronized (midLock) {
                    midResult += counter;
                }
            });
            Thread thread2 = new Thread(()-> {
                int counter = 0;
                for (int i = line.length()/2; i <= line.length()*3/ 4; i++) {
                    if (line.charAt(i) == '(') {
                        counter++;
                    } else {
                        counter--;
                    }
                }
                synchronized (midLock) {
                    midResult -= counter;
                }
            });

            try {
                thread1.start();
                thread2.start();

                thread1.join();
                thread2.join();
                if (midResult < 0) {
                    throw new IllegalStateException("Incorrect");
                }
                //System.out.println("Mid result for second 1/2 :" + midResult );
                synchronized (lock) {
                    result -= midResult;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    public long runParallel() throws InterruptedException {
        long start = System.nanoTime();

        Thread t1 = new Thread(new Handler1());
        Thread t2 = new Thread(new Handler2());

        try {
            t1.start();
            t2.start();

            t1.join();
            t2.join();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }


        long end = System.nanoTime();
        long resultTime = end - start;
        if (result == 0) {
            System.out.println("Correct");
        } else {
            System.out.println("Wrong");
        }
        System.out.println("Parallel method takes " + resultTime + " nanosec");
        return resultTime;
    }
}
