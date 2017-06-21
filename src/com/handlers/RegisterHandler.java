package com.handlers;

import com.Message;
import com.server.MessageWithSocketChannel;
import com.server.Server;

import java.nio.channels.SocketChannel;
import java.util.Map;

public class RegisterHandler implements Handler, Runnable {
    private Map<String, SocketChannel> clients;
    MessageWithSocketChannel pair;

    @Override
    public void setMessage(MessageWithSocketChannel pair) {
        this.pair = pair;
    }

    @Override
    public void run() {
        processMessage();
    }

    public RegisterHandler(Map<String, SocketChannel> clients) {
        this.clients = clients;
    }

    @Override
    public void processMessage() {
        clients.put(pair.getMessage().getNickname(), pair.getSocketChannel());
        logInfo("new participant: " + pair.getMessage().getNickname());

        try {
            String clientsAsString = String.join(", ", clients.keySet());
            Message response = new Message(Message.Type.REGISTER_RESPONSE, clientsAsString, "server");
            // send chat participant list to each participant
            for (Map.Entry<String, SocketChannel> client : clients.entrySet()) {
                SocketChannel socketChannel = client.getValue();
                CommonActions.fullBuffers(response, socketChannel);
                Server.readyToWrite(socketChannel);
                logInfo("List of participants: [" + response.toString() + "] was sent to [" + client.getKey() + "]");
            }
        } catch (Exception e) {
            logError("Register request failed.");
            e.printStackTrace();
        }
    }

    private void logInfo(String str) {
        System.out.println(this.getClass().getName() + " INFO: " + str + ".");
    }

    private void logError(String str) {
        System.out.println(this.getClass().getName() + " ERROR: " + str + ".");
    }
}
