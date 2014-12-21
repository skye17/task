package task;


public class SlowChecker {
    public long run(StringBuilder line) {
        int balanceCounter = 0;
        int length = line.length();

        long startTime = System.nanoTime();

        for (int i = 0; i < length; i++) {
            if (line.charAt(i) == '(') {
                ++balanceCounter;
            } else {
                if (line.charAt(i) == ')') {
                    --balanceCounter;
                }
            }
            if (balanceCounter < 0)
                break;
        }
        long endTime = System.nanoTime();

        long resultTime = endTime - startTime;
        if (balanceCounter == 0) {
            System.out.println("Correct");
        } else {
            System.out.println("Wrong");
        }

        return resultTime;
    }
}
