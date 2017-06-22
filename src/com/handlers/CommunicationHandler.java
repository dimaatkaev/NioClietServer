package com.handlers;

import com.Message;
import com.server.MessageWithSocketChannel;
import com.server.Server;

import java.io.IOException;
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
            String recipient = message.getNickname();
            SocketChannel socketChannel = clients.get(recipient);
            CommonActions.fullBuffers(message, socketChannel);
            Server.readyToWrite(socketChannel);
            logInfo("Communication message: " + message + " was sent to " + recipient + ".");
        } catch (IOException e) {
            logWarn("Communication message failed due connection problem. Message: " + message);
            e.printStackTrace();
        } catch (Exception e) {
            logError("Communication message failed. Message: " + message);
        }
    }

    private static void logInfo(String logline) {
        System.out.println(CommunicationHandler.class.getName() + " INFO: [" + logline + ".");
    }

    private static void logWarn(String logline) {
        System.out.println(CommunicationHandler.class.getName() + " WARN: " + logline + ".");
    }

    private static void logError(String logline) {
        System.out.println(CommunicationHandler.class.getName() + " ERROR: " + logline + ".");
    }
}
