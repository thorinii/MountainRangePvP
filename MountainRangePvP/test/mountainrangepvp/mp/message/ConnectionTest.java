/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author lachlan
 */
public class ConnectionTest {

    private MessageServer server;
    private MessageClient client;

    @Test
    @Ignore
    public void test() throws IOException, InterruptedException {
        startServer();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);

        client = new MessageClient("localhost");
        client.addMessageListener(new TestMessageListener(latch, success));
        client.start();

        while (latch.getCount() == 1) {
            client.update();
            Thread.sleep(10);
        }

        client.stop();
        server.stop();

        if (!success.get())
            fail("Connection/Handshake Failed");
    }

    private void startServer() throws IOException {
        server = new MessageServer();
        server.start();
    }

    private static class TestMessageListener implements MessageListener {

        private final CountDownLatch latch;
        private final AtomicBoolean success;

        public TestMessageListener(CountDownLatch latch, AtomicBoolean success) {
            this.latch = latch;
            this.success = success;
        }

        @Override
        public void accept(Message message, int id) throws IOException {
            if (message instanceof ServerHelloMessage) {
                ServerHelloMessage shm = (ServerHelloMessage) message;

                if (shm.isValid())
                    success.set(true);

                latch.countDown();
            } else {
                System.out.println(message);
                latch.countDown();
            }
        }
    }
}
