package com.handlers;

import com.Message;
import com.MessageUtils;
import com.server.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class CommonActions {
    public static void fullBuffers(Message response, SocketChannel socketChannel) throws IOException {
        ByteBuffer[] message = MessageUtils.splitToByteBuffer(response);
        ByteBuffer[] buffers = Server.getClientBuffers(socketChannel).getToWrite();
        for (int i = 0; i < message.length; i++) {
            buffers[i].clear();
            buffers[i].put(message[i]);
            buffers[i].flip();
        }
    }
}
