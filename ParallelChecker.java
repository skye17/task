package task;

public class ParallelChecker {
    private final Object lock = new Object();
    private StringBuilder line;
    private int length;
    private int childThreadsNumber;
    private int intervalLength;
    private int resultBalance;

    class HalfHandler implements Runnable {
        /*
        * Balance on the half of given sequence.
         */
        int balance;
        /*
        * Needed for the same behaviour when go forward and backwards.
         */
        int corrector;
        char positiveBracket = '(';
        /*
        * isForward  = :
        * true - thread goes through line forward.
        * false - thread goes through line backwards.
         */
        final boolean isForward;
        /*
        * Keeps maximum offset from zero balance in the intervals.
        * It's needed for checking if sequence is correct or not.
         */
        int[] maxIntervalOffsets = new int[childThreadsNumber - 1];

        /*
        * Keeps balances on the intervals.
         */
        int[] intervalBalances = new int[childThreadsNumber - 1];

        final Object midLock = new Object();

        HalfHandler(boolean isForward) {
            this.isForward = isForward;
            if (!isForward) {
                corrector = length - 1;
                positiveBracket = ')';
            }
        }

        /*
        * Thread that process border interval of string.
        * If isForward then left border otherwise right.
         */
        Runnable boundary = () -> {
            int balanceCounter = 0;
            for (int i = 0; i < intervalLength; i++) {
                if (line.charAt(Math.abs(i-corrector) ) == positiveBracket) {
                    balanceCounter++;
                } else {
                    balanceCounter--;
                }
                if (balanceCounter < 0) {
                    throw new IllegalStateException("Incorrect");
                }
            }
            synchronized (midLock) {
                balance += balanceCounter;
            }
        };

        /*
        * Thread that process inner interval of given string.
         */
        class Insider implements Runnable {
            final int number;
            Insider(int number) {
                this.number = number;
            }
            @Override
            public void run() {
                int min = Integer.MAX_VALUE;
                int counter = 0;
                int startIndex = number*intervalLength;
                int endIndex = startIndex + intervalLength;
                if (number == childThreadsNumber - 1) {
                    endIndex = length / 2;
                }

                for (int i = startIndex; i < endIndex; i++) {
                    if (line.charAt(Math.abs(i-corrector)) == positiveBracket) {
                        counter++;
                    } else {
                        counter--;
                    }
                    if (counter < min) {
                        min = counter;
                    }
                }
                synchronized (midLock) {
                    maxIntervalOffsets[number - 1] = min;
                    intervalBalances[number - 1] = counter;
                }
            }
        }

        @Override
        public void run() {

            Thread[] insideThreads = new Thread[childThreadsNumber];
            insideThreads[0] = new Thread(boundary);

            for (int i = 1; i < childThreadsNumber; i++) {
                insideThreads[i] = new Thread(new Insider(i));
            }

            for (int i = 0; i < childThreadsNumber; i++) {
                insideThreads[i].start();
            }

            for  (int i = 0; i < childThreadsNumber; i++) {
                try {
                    insideThreads[i].join();
                } catch (InterruptedException e) {}
            }

            for (int i = 0; i < childThreadsNumber - 1; i++) {
                if (balance + maxIntervalOffsets[i] < 0) {
                    throw new IllegalStateException("Incorrect");
                }
                balance += intervalBalances[i];
            }

            if (balance < 0) {
                throw new IllegalStateException("Incorrect");
            }

            synchronized (lock) {
                if (isForward) {
                    resultBalance += balance;
                } else {
                    resultBalance -= balance;
                }
            }
        }
    }

    public long runParallel(StringBuilder sequence, int threadsNumber) throws InterruptedException {
        line = sequence;
        length = line.length();
        childThreadsNumber = threadsNumber/2;
        intervalLength = length/threadsNumber;
        Thread firstPartThread = new Thread(new HalfHandler(true));
        Thread secondPartThread = new Thread(new HalfHandler(false));

        long startTime = System.nanoTime();
        try {
            firstPartThread.start();
            secondPartThread.start();

            firstPartThread.join();
            secondPartThread.join();
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

        return resultTime;
    }
}
