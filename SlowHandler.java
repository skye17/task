package task;


public class SlowHandler {
    public long run(StringBuilder line) {
        int counter = 0;
        long start = System.nanoTime();
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '(') {
                ++counter;
            } else {
                if (line.charAt(i) == ')') {
                    --counter;
                }
            }
            if (counter < 0)
                break;
        }
        long end = System.nanoTime();
        long resultTime = end - start;
        if (counter == 0) {
            System.out.println("Correct");
        } else {
            System.out.println("Wrong");
        }
        System.out.println("Slow method takes " + resultTime + " nanosec");
        return resultTime;
    }
}
