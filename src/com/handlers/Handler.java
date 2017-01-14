package com.handlers;

import com.MessageWithSocketChannel;

public interface Handler extends Runnable {
    void processMessage();

    void setMessage(MessageWithSocketChannel message);
}
