package com.dima;

import com.dima.messaging.Message;
import com.dima.messaging.MessageUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private MessageRouter messageRouter = new MessageRouter();
    private final static int INIT_PORT = 1111;

    public static void main(String[] args) {
        try {
            new Server().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void go() throws IOException, ClassNotFoundException {
        // Selector: multiplexor of SelectableChannel objects
        Selector selector = Selector.open(); // selector is open here

        // ServerSocketChannel: selectable channel for stream-oriented listening sockets
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        InetSocketAddress serverAddr = new InetSocketAddress("localhost", INIT_PORT);

        // Binds the channel's socket to a local address and configures the socket to listen for connections
        serverSocket.bind(serverAddr);

        // Adjusts this channel's blocking mode.
        serverSocket.configureBlocking(false);

        int ops = serverSocket.validOps();
        /*SelectionKey selectKy = */serverSocket.register(selector, ops, null);

        logInfo("Start message router");
        messageRouter.start();

        // Infinite loop..
        // Keep server running
        while (true) {

            logInfo("i'm a server and i'm waiting for new connection and buffer select...");
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

                    // Register client socket
                    client.register(selector, SelectionKey.OP_READ);
                    logInfo("Connection Accepted: " + client.getLocalAddress());
                } else if (myKey.isReadable()) {
                    SocketChannel client = (SocketChannel) myKey.channel();
                    Message inMessage = MessageUtils.getMessage(client);
                    messageRouter.addMessageInQueue(inMessage, client);

                    // switch socket to write
                    client.register(selector, SelectionKey.OP_WRITE);
                } else if (myKey.isWritable()) {
                    SocketChannel client = (SocketChannel) myKey.channel();

                    // switch socket to read
                    client.register(selector, SelectionKey.OP_READ);
                }
                keysIterator.remove();
            }
        }
    }

    private void logInfo(String str) {
        System.out.println(this.getClass().getName() + " INFO: " + str + ".");
    }
}
