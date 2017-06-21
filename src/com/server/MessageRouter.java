package com.server;

import com.Message;
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
        handlers.put(Message.Type.REGISTER_REQUEST, new RegisterHandler(clients));
        handlers.put(Message.Type.COMMUNICATION, new CommunicationHandler(clients));
    }

    public void addMessageInQueue(Message message, SocketChannel socketChannel) {
        handleMessages.add(new MessageWithSocketChannel(message, socketChannel));
    }

    @Override
    public void run() {
        logInfo("MessageRouter started.");
        while (true) {
            if (!handleMessages.isEmpty()) {
                MessageWithSocketChannel pollValue = (MessageWithSocketChannel) handleMessages.poll();

                boolean isHandled = false;
                for (Message.Type handlerType : handlers.keySet()) {
                    if (pollValue.getMessage().getType() == handlerType) {
                        Handler handler = handlers.get(handlerType);
                        exeMessage(handler, pollValue);
                        executor.execute(handler);

                        // FIXME: is it necessary
                        isHandled = true;
                    }
                }

                // FIXME: is it necessary to do this check
                if (!isHandled) {
                    logInfo("Could not find appropriate handler to process message" + pollValue.getMessage().toString());
                }
            }
        }
    }

    private void exeMessage(Handler handler, MessageWithSocketChannel pollValue) {
        handler.setMessage(pollValue);
        logInfo("Execute " + pollValue.getMessage());
    }

    private void logInfo(String str) {
        System.out.println(this.getClass().getName() + "INFO: " + str + ".");
    }
}
