package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.MessageUtils.sendMessage;

public class Client {

    private String name;
    private List<String> chatMembers = new ArrayList<>();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static final String REGISTER_MESSAGE = "register";
    private static final String EXIT_PHRASE = "exit";
    private static final int WAIT_TIME = 1000;
    private static final int INIT_PORT = 1111;

    private boolean isFinish = false;

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        new Client().go();
    }

    private static void log(String str) {
        System.out.println(str);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChatMembers(List<String> chatMembers) {
        this.chatMembers = chatMembers;
    }

    private void go() throws IOException, ClassNotFoundException, InterruptedException {
        InetSocketAddress serverAdr = new InetSocketAddress("localhost", INIT_PORT);
        SocketChannel client = connectToServer(serverAdr);

        log("Connecting to com.Server on port " + INIT_PORT + "...");

        sendRegisterMessage(client);

        Thread listener = new SocketListener(client);
        listener.start();

        // waiting for register request
        while (chatMembers.isEmpty()) {
            Thread.sleep(500);
        }

        askCommunicationMessage(client, listener);

        client.close();
    }

    private void askCommunicationMessage(SocketChannel client, Thread listener) throws IOException {
        while (true) {
            if (isFinish) {
                listener.interrupt();
                client.close();
                break;
            }

            log("please choose recipient: " + String.join(", ", chatMembers));
            String recipient = readLine();
            if (!chatMembers.contains(recipient)) {
                log("There is no recipient with entered name");
                continue;
            }

            log("Please write message");
            String text = readLine();
            sendMessage(client, new Message(Message.Type.COMMUNICATION, text, recipient));
            log("sent text = " + text);
        }
    }

    private void sendRegisterMessage(SocketChannel client) throws IOException {
        log("Please, enter your nickname");
        setName(readLine());
        sendMessage(client, new Message(Message.Type.REGISTER_REQUEST, REGISTER_MESSAGE, name));
        log("sent register message, name = " + name);
    }

    private List<String> getChatMembers(String list) {
        String[] strings = list.split(", ");
        return Arrays.asList(strings);
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
                        setChatMembers(getChatMembers(inMessage.getText()));
                    } else {
                        log("incoming message: " + inMessage.getText());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    log("ERROR income message failed");
                    e.printStackTrace();
                }
            }
        }
    }

    private String readLine() throws IOException {
        String in = reader.readLine();
        if (in.equals(EXIT_PHRASE)) {
            isFinish = true;
        }
        return in;
    }

    private SocketChannel connectToServer(InetSocketAddress serverAddr) throws InterruptedException {
        SocketChannel client;
        while (true) {
            try {
                client = SocketChannel.open(serverAddr);
                break;
            } catch (IOException e) {
                log("Can not connect to server ...");
                Thread.sleep(WAIT_TIME);
            }
        }
        return client;
    }
}
