package task;

public class PSPHandler {
    public static void main(String[] args) throws InterruptedException {
        String pattern = "(())()";
        int threadsNumber = 4;
        StringBuilder line = new StringBuilder(Integer.MAX_VALUE/5);
        for (int i = 0; i < Integer.MAX_VALUE/20; i++) {
            line.append(pattern);
        }
        long first = new SlowChecker().run(line);
        System.out.println("Slow method takes " + first + " nanosec");

        long second = new ParallelChecker().runParallel(line, threadsNumber);
        System.out.println("Parallel method takes " + second + " nanosec");

        System.out.println("Parallel is " + (first * 1.0d)/second + " faster than slow");
    }
}
