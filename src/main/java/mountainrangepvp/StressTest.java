package mountainrangepvp;

import mountainrangepvp.util.Log;
import mountainrangepvp.game.mp.GameClient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

/**
 * @author lachlan
 */
public class StressTest {

    private static final int DEFAULT_CLIENTS_TO_SPAWN = 30;

    public static void stressTest(final String host, int num) {
        try {
            stressTestUnsafe(host, num);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to fully execute stress test", e);
        }
    }

    private static void stressTestUnsafe(final String host, int num) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(num);

        for (int i = 0; i < num; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        GameClient.startClient(host);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            }, "GameClient " + i).start();
            System.out.println("Started GameClient " + i);
            Thread.sleep(1000);
        }

        latch.await();
    }

    public static void main(String[] args) throws InterruptedException {
        Log.setupLog(Level.WARNING);

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
}
