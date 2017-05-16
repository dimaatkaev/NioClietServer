package com.handlers;

import com.Message;
import com.MessageUtils;
import com.MessageWithSocketChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class CommunicationHandler implements Handler, Runnable {
    private Map<String, SocketChannel> clients;
    private Message message;

    public CommunicationHandler(Map<String, SocketChannel> clients) {
        this.clients = clients;
    }

    @Override
    public void setMessage(MessageWithSocketChannel pair) {
        message = pair.getMessage();
    }

    @Override
    public void run() {
        processMessage();
    }

    @Override
    public void processMessage() {
        try {
            byte[] communicationMessage = MessageUtils.getMessageAsByteArray(message);
            SocketChannel socketChannel = clients.get(message.getNickname());

            ByteBuffer communicationBuffer = ByteBuffer.wrap(communicationMessage);
            boolean sent = true;
            while (sent) {
                if (socketChannel.isConnected()) {
                    socketChannel.write(communicationBuffer);
                    //TODO Add buffer message read
                    sent = false;
                    log("sending communication message: " + message + " sent.");
                }
            }
            communicationBuffer.clear();
        } catch (IOException e) {
            log("ERROR communication message failed");
            e.printStackTrace();
        }
    }

    private static void log(String str) {
        System.out.println(str);
    }
}
