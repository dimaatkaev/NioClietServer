package com.dima.handlers;

import com.dima.messaging.Message;
import com.dima.MessageWithSocketChannel;
import com.dima.messaging.MessageFactory;

import java.nio.channels.SocketChannel;
import java.util.Map;

import static com.dima.messaging.MessageUtils.sendMessage;

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
        clients.put(pair.getMessage().getSender(), pair.getSocketChannel());
        logInfo("new participant: " + pair.getMessage().getSender());

        try {
            String clientsAsString = String.join(", ", clients.keySet());
            Message listOfParticipants = MessageFactory.getRegisterResponse(clientsAsString);
            // send chat participant list to each participant
            for (Map.Entry<String, SocketChannel> client : clients.entrySet()) {
                SocketChannel socketChannel = client.getValue();
                boolean sent = true;
                while (sent) {
                    sendMessage(socketChannel, listOfParticipants);
                    sent = false;
                    logInfo("List of participants: [" + listOfParticipants.toString() + "] was sent to [" + client.getKey() + "]");
                }
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
