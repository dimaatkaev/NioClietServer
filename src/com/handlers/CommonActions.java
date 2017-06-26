package com.handlers;

import com.Message;
import com.MessageUtils;
import com.server.ClientBuffers;
import com.server.ClientBuffersFactory;
import com.server.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class CommonActions {
    public static void fillBuffers(Message response, SocketChannel socketChannel) throws IOException {
        ByteBuffer[] message = MessageUtils.splitToByteBuffer(response);

        ClientBuffers clientBuffers = Server.getClientBuffers(socketChannel);
        ByteBuffer[] buffers = clientBuffers.getToWrite();

        while (buffers.length < message.length) {
            clientBuffers = ClientBuffersFactory.addOneBufferToWrite(clientBuffers);
            buffers = clientBuffers.getToWrite();
        }
        while (buffers.length > message.length) {
            clientBuffers = ClientBuffersFactory.removeOneBufferToWrite(clientBuffers);
            buffers = clientBuffers.getToWrite();
        }
        if (buffers.length == message.length) {
            for (int i = 0; i < message.length; i++) {
                buffers[i].clear();
                buffers[i].put(message[i]);
                buffers[i].flip();
            }
        } else {
            logFatal("Redundant buffer in ClientBuffers.");
        }
    }

    public static void logFatal(String log) {
        System.out.println(Class.class.getName() + ": FATAL. " + log);
    }
}
