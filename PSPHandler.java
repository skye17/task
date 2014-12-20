package task;

public class PSPHandler {
    public static void main(String[] args) throws InterruptedException {
        String pattern = "(())()";
        StringBuilder line = new StringBuilder(Integer.MAX_VALUE/5);
        for (int i = 0; i < Integer.MAX_VALUE/30; i++) {
            line.append(pattern);
        }
        long first = new SlowHandler().run(line);

        long second = new ParallelHandler(line).runParallel();
        System.out.println("Parallel is " + (first * 1.0d)/second + " faster than slow");
    }
}
