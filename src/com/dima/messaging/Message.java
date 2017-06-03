package com.dima.messaging;

import java.io.Serializable;

public class Message implements Serializable {

    private final Type type;
    private final String text;
    private final String sender;
    private final String recipient;

    public Message(Type type, String text, String sender, String recipient) {
        this.type = type;
        this.text = text;
        this.sender = sender;
        this.recipient = recipient;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public enum Type {
        REGISTER_REQUEST,
        REGISTER_RESPONSE,
        COMMUNICATION
    }

    @Override
    public String toString() {
        return "Message: " +
                "type=" + type +
                ", text='" + text + '\'' +
                ", sender='" + sender + '\'';
    }

    public String getView() {
        return getSender() + " : " + getText();
    }
}
