package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {

    private String name;
    private List<String> chatMembers = new ArrayList<>();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static final String REGISTER_MESSAGE = "register";
    private static final String EXIT_PHRASE = "exit";
    private static final int WAIT_TIME = 1000;
    private static final int BYTE_BUFFER_CAPACITY = 256;

    private boolean isFinish = false;

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        new Client().clientAction();
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

    private void clientAction() throws IOException, ClassNotFoundException, InterruptedException {
        InetSocketAddress serverAddr = new InetSocketAddress("localhost", 1111);
        SocketChannel client = connectToServer(serverAddr);

        log("Connecting to com.Server on port 1111...");

        //send register message
        log("Please, enter your nickname");
        setName(readLine());

        byte[] message = Message.getMessageAsByteArray(createRegisterMessage());
        ByteBuffer buffer = ByteBuffer.wrap(message);
        client.write(buffer);
        log("sent register message, name = " + name);
        buffer.clear();

        Thread listener = new SocketListener(client);
        listener.start();

        while (true) {
            if (isFinish) {
                listener.interrupt();
                client.close();
                break;
            }
            String recipient = readLine();
            if (!chatMembers.contains(recipient)) {
                log("There is no recipient with entered name");
                continue;
            }
            System.out.println("Please write message");
            String text = readLine();
            byte[] communicationMessage = Message.getMessageAsByteArray(new Message(Message.Type.COMMUNICATION, text, recipient));
            ByteBuffer communicationBuffer = ByteBuffer.wrap(communicationMessage);
            //TODO Add buffer to split message
            client.write(communicationBuffer);
            communicationBuffer.clear();
            log("sent text = " + text);
        }
        client.close();
    }

    private List<String> getChatMembers(String list) {
        String[] strings = list.split(", ");
        return Arrays.asList(strings);
    }

    private Message createRegisterMessage() {
        return new Message(Message.Type.REGISTER, REGISTER_MESSAGE, name);
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
                    ByteBuffer clientBuffer = ByteBuffer.allocate(BYTE_BUFFER_CAPACITY);
                    client.read(clientBuffer);
                    Message inMessage = Message.getMessageFromByteArray(clientBuffer.array());
                    //TODO buffered message
                    if (inMessage.getType().equals(Message.Type.REGISTER_REQUEST)) {
                        setChatMembers(getChatMembers(inMessage.getText()));
                        log("please choose recipient: " + String.join(", ", chatMembers));
                    } else {
                        log("incoming message: " + inMessage.getText());
                    }
                    clientBuffer.clear();
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
