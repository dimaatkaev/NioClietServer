package com.server;

import com.Message;
import com.MessageUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.MessageUtils.findTerminatorPosition;

public class Server {

    private static final Map<SocketChannel, ClientBuffers> sockets = new ConcurrentHashMap<>();
    private MessageRouter messageRouter = new MessageRouter();
    private static Selector selector;

    private final static int INIT_PORT = 1111;

    public static void main(String[] args) {
        try {
            new Server().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void go() throws IOException, ClassNotFoundException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        InetSocketAddress serverAddr = new InetSocketAddress("localhost", INIT_PORT);
        serverChannel.bind(serverAddr);
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        messageRouter.start();
        while (true) {
            logInfo("Server waiting for new connection and buffer select.");
            // Selects a set of keys whose corresponding channels are ready for I/O operations
            int readyChannels = selector.select();
            if(readyChannels == 0) continue;
            Set<SelectionKey> clientKeys = selector.selectedKeys();
            Iterator<SelectionKey> keysIterator = clientKeys.iterator();

            while (keysIterator.hasNext()) {
                SelectionKey key = keysIterator.next();
                try {
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverChannel.accept();
                        socketChannel.configureBlocking(false);
                        registerSocketChannel(socketChannel);
                        logInfo("Connection Accepted: " + socketChannel.getRemoteAddress());
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        // TODO implement read whole array
                        ByteBuffer buffer = getClientBuffers(socketChannel).getToRead()[0];
                        int bytesRead = socketChannel.read(buffer);
                        logInfo("Reading from " + socketChannel.getRemoteAddress() + ", bytes read=" + bytesRead);

                        // Detecting connection closed from client side
                        if (bytesRead == -1) {
                            removeSocketChennal(socketChannel);
                        }

                        // Detecting end of the message
                        if (bytesRead > 0 && findTerminatorPosition(buffer.array()) != -1) {
                            Message incomingMessage = MessageUtils.getMessageFromByteArray(
                                    // TODO implement cycle
                                    getClientBuffers(socketChannel).getToRead()[0].array()
                            );
                            getClientBuffers(socketChannel).getToRead()[0].clear();
                            messageRouter.addMessageInQueue(incomingMessage, socketChannel);
                        }
                    } else if (key.isWritable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        // TODO race condition someware around here
                        ByteBuffer[] buffers = getClientBuffers(socketChannel).getToWrite();

                        long bytesWritten = socketChannel.write(buffers); // woun't always write anything
                        logInfo("Writing to " + socketChannel.getRemoteAddress() + ", bytes written = " + bytesWritten);

                        ByteBuffer lastBuffer = buffers[buffers.length - 1];
                        if (!lastBuffer.hasRemaining()) {
                            lastBuffer.compact();
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                    }
                } catch (IOException e) {
                    logError(e.getMessage() + " " + e.getStackTrace());
                } catch (ClassNotFoundException e) {
                    logError(e.getMessage() + " " + e.getStackTrace());
                }
                keysIterator.remove();
            }
        }
    }

    public static void registerSocketChannel(SocketChannel socketChannel) {
        sockets.put(socketChannel, ClientBuffersFactory.getClientBuffers()); // Allocating buffer for socket channel
    }

    public static ClientBuffers getClientBuffers(SocketChannel socketChannel) {
        return sockets.get(socketChannel);
    }

    public static void removeSocketChennal(SocketChannel socketChannel) throws IOException {
        logInfo("Connection closed " + socketChannel.getRemoteAddress());
        sockets.remove(socketChannel);
        socketChannel.close();
    }

    public static void readyToWrite(SocketChannel socketChannel) throws IOException {
        socketChannel.register(selector, SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    private static void logInfo(String str) {
        System.out.println(Server.class.getName() + " INFO: [" + Thread.currentThread().getName() + "] " + str);
    }

    private static void logError(String str) {
        System.out.println(Server.class.getName() + " ERROR: [" + Thread.currentThread().getName() + "] " + str);
    }
}
