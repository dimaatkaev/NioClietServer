package com.handlers;

import com.server.MessageWithSocketChannel;

public interface Handler extends Runnable {
    void processMessage();

    void setMessage(MessageWithSocketChannel message);
}
