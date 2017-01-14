package com.handlers;

import com.Message;
import com.MessageRouter;
import com.MessageWithSocketChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
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
        log("new participant: " + pair.getMessage().getNickname());

        try {
            String clientsAsString = String.join(", ", clients.keySet());
            Message request = new Message(Message.Type.REGISTER_REQUEST, clientsAsString, "server");
            byte[] requestBytes = Message.getMessageAsByteArray(request);
            ByteBuffer communicationBuffer;
            //send chat participant list to each participant
            for (Map.Entry<String, SocketChannel> client : clients.entrySet()) {
                SocketChannel socketChannel = client.getValue();
                communicationBuffer = ByteBuffer.wrap(requestBytes);
                boolean sent = true;
                while (sent) {
                    if (socketChannel.isConnected()) {
                        socketChannel.write(communicationBuffer);
                        sent = false;
                        log("sending register request message: " + request.toString() + " sent.");
                    }
                }
                communicationBuffer.clear();
            }
        } catch (IOException e) {
            log("ERROR register request failed");
            e.printStackTrace();
        }
    }

    private static void log(String str) {
        System.out.println(str);
    }
}
