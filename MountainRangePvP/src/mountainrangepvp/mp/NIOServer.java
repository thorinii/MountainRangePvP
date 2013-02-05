/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

/**
 *
 * @author lachlan
 */
public class NIOServer {

    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private final ByteBuffer readBuffer;

    public NIOServer() throws IOException {
        selector = SelectorProvider.provider().openSelector();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        InetSocketAddress address = new InetSocketAddress(
                MultiplayerConstants.STD_PORT);
        serverChannel.bind(address);

        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        readBuffer = ByteBuffer.allocate(2048);
    }

    public void update() {
        try {
            doNIOSelection();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void doNIOSelection() throws IOException {
        selector.select();

        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
        while (keys.hasNext()) {
            SelectionKey key = keys.next();
            keys.remove();

            if (key.isValid()) {
                if (key.isAcceptable()) {
                    acceptNewClient(key);
                } else if (key.isReadable()) {
                    read(key);
                }
            }
        }
    }

    private void acceptNewClient(SelectionKey key) throws IOException {
        ServerSocketChannel serverchannel = (ServerSocketChannel) key.channel();

        SocketChannel socketChannel = serverchannel.accept();
        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        this.readBuffer.clear();

        try {
            int numRead = socketChannel.read(this.readBuffer);

            if (numRead == -1) {
                socketChannel.close();
                key.cancel();
            }
        } catch (IOException e) {
            socketChannel.close();
        }
    }
}
