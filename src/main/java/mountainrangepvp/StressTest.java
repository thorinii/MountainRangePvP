package mountainrangepvp;

/**
 * @author lachlan
 */
public class StressTest {

    private static final int DEFAULT_CLIENTS_TO_SPAWN = 30;

    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int num = DEFAULT_CLIENTS_TO_SPAWN;

        switch (args.length) {
            case 2:
                num = Integer.parseInt(args[1]);
            case 1:
                host = args[0];
        }

        stressTest(host, num);
    }

    public static void stressTest(String host, int clients) {
        throw new UnsupportedOperationException("StressTest is broken");
    }
}
