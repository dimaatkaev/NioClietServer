package com.dima;

import com.dima.messaging.Message;

import java.nio.channels.SocketChannel;

public class MessageWithSocketChannel {
    private final Message message;
    private final SocketChannel socketChannel;

    public MessageWithSocketChannel(Message message, SocketChannel socketChannel) {
        this.message = message;
        this.socketChannel = socketChannel;
    }

    public Message getMessage() {
        return message;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }
}
