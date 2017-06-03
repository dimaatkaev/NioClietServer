package com.dima.clientGUI;

import com.dima.messaging.Message;
import com.dima.messaging.MessageFactory;
import com.dima.messaging.MessageUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dima.messaging.MessageUtils.sendMessage;

public class Manager {
    private static Manager instance;
    private String sender;
    private List<String> participants = new ArrayList<>();
    private SocketChannel clientSocket;
    private List<Message> incomingMessages = new ArrayList<>();

    private static final String REGISTER_MESSAGE = "register";
    private static final int WAIT_TIME = 1000;
    private static final int INIT_PORT = 1111;

    private Manager() {
        openSocket();
    }

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    public void sendRegisterMessage() throws IOException {
        logInfo("Please, enter your sender");
        if (getSender() != null) {
            setSender(getSender());
        } else {
            throw new RuntimeException("Nickname required");
        }
        Message registerMessage = MessageFactory.getRegisterRequestMessage(sender);
        sendMessage(clientSocket, registerMessage);
        logInfo("sent register message, name = " + getSender());

        // start listen for messages
        Thread listener = new Manager.SocketListener(clientSocket);
        listener.start();
    }

    public void sendCommunicationMessage(String recipient, String text) throws IOException {
        if (!getParticipants().contains(recipient)) {
            logInfo("There is no recipient with entered name");
        }

        Message communicationMessage = MessageFactory.getCommunicationMessage(text, sender, recipient);
        sendMessage(clientSocket, communicationMessage);
        logInfo("sending " + communicationMessage.toString());
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<String> getParticipants() {
        // remove own name
        // TODO uncomment removing itself
//        participants.remove(sender);
        return participants;
    }

    private void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    private void openSocket() {
        try {
            InetSocketAddress serverAdr = new InetSocketAddress("localhost", INIT_PORT);
            SocketChannel client = connectToServer(serverAdr);
            this.clientSocket = client;
            logInfo("Connecting to com.dima.Server on port " + INIT_PORT + "...");
        } catch (InterruptedException e) {
            throw new RuntimeException("Connecting to com.dima.Server on port " + INIT_PORT + " failed.");
        }
    }

    private SocketChannel connectToServer(InetSocketAddress serverAddr) throws InterruptedException {
        SocketChannel client;
        while (true) {
            try {
                client = SocketChannel.open(serverAddr);
                break;
            } catch (IOException e) {
                logInfo("Can not connect to server ...");
                Thread.sleep(WAIT_TIME);
            }
        }
        return client;
    }

    class SocketListener extends Thread {
        private final SocketChannel client;

        public SocketListener(SocketChannel client) {
            this.client = client;
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    Message inMessage = MessageUtils.getMessage(client);

                    if (inMessage.getType().equals(Message.Type.REGISTER_RESPONSE)) {
                        String[] newParticipants = inMessage.getText().split(",");
                        setParticipants(Arrays.asList(newParticipants));
                    } else {
                        logInfo("incoming message: " + inMessage.getText());
                        incomingMessages.add(inMessage);
                    }
                } catch (IOException e) {
                    logInfo("Connection problem.");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Could not detect type of message. " + e.getStackTrace());
                } catch (Exception e) {
                    throw new RuntimeException("Unknown exception: " + e.getMessage() + e.getStackTrace());
                }
            }
        }
    }

    public List<Message> getIncomingMessages() {
        return incomingMessages;
    }

    private void logInfo(String str) {
        System.out.println(this.getClass().getName() + " INFO: " + str + ".");
    }
}
