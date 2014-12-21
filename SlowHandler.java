package task;


public class SlowHandler {
    public long run(StringBuilder line) {
        int counter = 0;
        int length = line.length();
        long startTime = System.nanoTime();
        for (int i = 0; i < length; i++) {
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
        long endTime = System.nanoTime();
        long resultTime = endTime - startTime;
        if (counter == 0) {
            System.out.println("Correct");
        } else {
            System.out.println("Wrong");
        }
        System.out.println("Slow method takes " + resultTime + " nanosec");
        return resultTime;
    }
}
