package com;

import java.io.Serializable;

public class Message implements Serializable {

    private final Type type;
    private final String text;
    private final String nickname;

    public Message(Type type, String text, String nickname) {
        this.type = type;
        this.text = text;
        this.nickname = nickname;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getNickname() {
        return nickname;
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
                ", nickname='" + nickname + '\'';
    }
}
