package com;

import com.handlers.CommunicationHandler;
import com.handlers.Handler;
import com.handlers.RegisterHandler;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageRouter extends Thread {
    private final static int POOL_CAPACITY = 5;
    private final static int QUEUE_SIZE = 10;

    private BlockingQueue handleMessages = new ArrayBlockingQueue<MessageWithSocketChannel>(QUEUE_SIZE);
    private Map<Message.Type, Handler> handlers = new HashMap<>();
    private Map<String, SocketChannel> clients = new HashMap<>();
    private ExecutorService executor = Executors.newFixedThreadPool(POOL_CAPACITY);

    public MessageRouter() {
        initHandlers();
    }

    public void initHandlers() {
        handlers.put(Message.Type.REGISTER, new RegisterHandler(clients));
        handlers.put(Message.Type.COMMUNICATION, new CommunicationHandler(clients));
    }

    public void addMessageInQueue(Message message, SocketChannel socketChannel) {
        handleMessages.add(new MessageWithSocketChannel(message, socketChannel));
    }

    @Override
    public void run() {
        while (true) {
            if (!handleMessages.isEmpty()) {
                MessageWithSocketChannel pollValue = (MessageWithSocketChannel) handleMessages.poll();
                if (pollValue.getMessage().getType() == Message.Type.REGISTER) {
                    Handler handler = handlers.get(Message.Type.REGISTER);
                    setMessageAndLog(handler, pollValue);
                    executor.execute(handler);
                } else if (pollValue.getMessage().getType() == Message.Type.COMMUNICATION) {
                    Handler handler = handlers.get(Message.Type.COMMUNICATION);
                    setMessageAndLog(handler, pollValue);
                    executor.execute(handler);
                } else {
                    throw new IllegalArgumentException("Incorrect handler type.");
                }
            }
        }
    }

    private void setMessageAndLog(Handler handler, MessageWithSocketChannel pollValue) {
        handler.setMessage(pollValue);
        log("Register message = " + pollValue.getMessage().toString());
    }

    private static void log(String str) {
        System.out.println(str);
    }
}
