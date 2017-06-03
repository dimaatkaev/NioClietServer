package com.dima.messaging;

public class MessageFactory {

    private static final String REGISTER_MESSAGE = "register";
    private static final String SERVER = "server";

    public static Message getCommunicationMessage(String text, String sender, String recipient) {
        return new Message(Message.Type.COMMUNICATION, text, sender, recipient);
    }

    public static Message getRegisterRequestMessage(String sender){
        // TODO remove stub
        return new Message(Message.Type.REGISTER_REQUEST, REGISTER_MESSAGE, sender, "stub");
    }

    public static Message getRegisterResponse(String participantsList) {
        // TODO remove stub
        return new Message(Message.Type.REGISTER_RESPONSE, participantsList, SERVER, "stub");
    }
}
