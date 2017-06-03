package com.dima.handlers;

import com.dima.MessageWithSocketChannel;

public interface Handler extends Runnable {
    void processMessage();

    void setMessage(MessageWithSocketChannel message);
}
