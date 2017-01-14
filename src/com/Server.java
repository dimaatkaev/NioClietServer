package com;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private MessageRouter messageRouter = new MessageRouter();
    private final static int BYTE_BUFFER_CAPACITY = 256;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new Server().serverAction();
    }

    private static void log(String str) {
        System.out.println(str);
    }

    private void serverAction() throws IOException, ClassNotFoundException {
        // Selector: multiplexor of SelectableChannel objects
        Selector selector = Selector.open(); // selector is open here

        // ServerSocketChannel: selectable channel for stream-oriented listening sockets
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        InetSocketAddress serverAddr = new InetSocketAddress("localhost", 1111);

        // Binds the channel's socket to a local address and configures the socket to listen for connections
        serverSocket.bind(serverAddr);

        // Adjusts this channel's blocking mode.
        serverSocket.configureBlocking(false);

        int ops = serverSocket.validOps();
        SelectionKey selectKy = serverSocket.register(selector, ops, null);

        log("Start message router");
        messageRouter.start();

        // Infinite loop..
        // Keep server running
        while (true) {

            log("i'm a server and i'm waiting for new connection and buffer select...");
            // Selects a set of keys whose corresponding channels are ready for I/O operations
            selector.select();

            // token representing the registration of a SelectableChannel with a Selector
            Set<SelectionKey> clientKeys = selector.selectedKeys();
            Iterator<SelectionKey> keysIterator = clientKeys.iterator();

            while (keysIterator.hasNext()) {
                SelectionKey myKey = keysIterator.next();

                // Tests whether this key's channel is ready to accept a new socket connection
                if (myKey.isAcceptable()) {
                    SocketChannel client = serverSocket.accept();

                    // Adjusts this channel's blocking mode to false
                    client.configureBlocking(false);

                    // Operation-set bit for read operations
                    client.register(selector, SelectionKey.OP_READ);
                    log("Connection Accepted: " + client.getLocalAddress());
                } else if (myKey.isReadable()) {
                    SocketChannel client = (SocketChannel) myKey.channel();
                    ByteBuffer clientBuffer = ByteBuffer.allocate(BYTE_BUFFER_CAPACITY);
                    client.read(clientBuffer);
                    Message message = Message.getMessageFromByteArray(clientBuffer.array());

                    messageRouter.addMessageInQueue(message, client);
                    client.register(selector, SelectionKey.OP_WRITE);
                } else if (myKey.isWritable()) {
                    SocketChannel client = (SocketChannel) myKey.channel();
                    client.register(selector, SelectionKey.OP_READ);
                }
                keysIterator.remove();
            }
        }
    }
}
