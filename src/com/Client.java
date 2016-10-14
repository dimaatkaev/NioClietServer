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

    private final int id;
    private String name;
    private List<String> chatMembers = new ArrayList<>();

    public Client(int id) {
        this.id = id;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new Client(1).clientAction();
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

    private void clientAction() throws IOException, ClassNotFoundException {
        InetSocketAddress serverAddr = new InetSocketAddress("localhost", 1111);
        SocketChannel client = SocketChannel.open(serverAddr);

        log("Connecting to com.Server on port 1111...");
        //send register message
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please, enter your nickname");
        setName(reader.readLine());

        byte[] message = Message.getMessageAsByteArray(createRegisterMessage());
        ByteBuffer buffer = ByteBuffer.wrap(message);
        client.write(buffer);
        log("sending register message name = " + name);
        buffer.clear();

        new SocketListener(client).start();

//        ByteBuffer clientBuffer = ByteBuffer.allocate(256);
//        if (client.isConnected()) {
//            client.read(clientBuffer);
//        }
//        Message registerRequest = Message.getMessageFromByteArray(clientBuffer.array());
//        setChatMembers(getChatMembers(registerRequest.getText()));
//        clientBuffer.clear();

        while (true) {
            System.out.println("please choose recipient: " + String.join(", ", chatMembers));
            String recipient = reader.readLine();
            if (!chatMembers.contains(recipient)) {
                continue;
            }
            System.out.println("Please write message");
            String text = reader.readLine();
            if (text.equals("exit")) {
                break;
            } else {
                byte[] communicationMessage = Message.getMessageAsByteArray(new Message(Message.Type.COMMUNICATION, text, recipient));
                ByteBuffer communicationBuffer = ByteBuffer.wrap(communicationMessage);
                client.write(communicationBuffer);
                communicationBuffer.clear();
                log("sending communication message text = " + text);

            }
        }
        client.close();
    }

    private List<String> getChatMembers(String list) {
        String[] strings = list.split(", ");
        return Arrays.asList(strings);
    }

    private Message createRegisterMessage() {
        String id = Integer.toString(this.id);
        return new Message(Message.Type.REGISTER, id, name);
    }

    class SocketListener extends Thread {
        private final SocketChannel socketChannel;

        public SocketListener(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            try {
                ByteBuffer clientBuffer = ByteBuffer.allocate(256);
                if (socketChannel.isConnected()) {
                    socketChannel.read(clientBuffer);
                }
                Message inMessage = Message.getMessageFromByteArray(clientBuffer.array());
                if (inMessage.getType().equals(Message.Type.REGISTER_REQUEST)) {
                    setChatMembers(getChatMembers(inMessage.getText()));
                } else {
                    log("We get a message: " + inMessage.toString());
                }
                clientBuffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}
