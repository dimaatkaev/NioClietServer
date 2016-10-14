package com;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.channels.SocketChannel;

public class Message implements Serializable {

	private final Type type;
	private final String text;
	transient private SocketChannel socketChannel = null;
	private final String nickname;

	public Message(Type type, String text, String nickname) {
		this.type = type;
		this.text = text;
		this.nickname = nickname;
	}

	public static byte[] getMessageAsByteArray(Message message) throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bo);
		out.writeObject(message);
		return bo.toByteArray();
	}

	public static Message getMessageFromByteArray(byte[] array) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bi = new ByteArrayInputStream(array);
		ObjectInputStream in = new ObjectInputStream(bi);
		return (Message) in.readObject();
	}

	public Type getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public String getNickname() {
		return nickname;
	}

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public enum Type {
		REGISTER,
		COMMUNICATION,
		REGISTER_REQUEST
	}

	@Override
	public String toString() {
		return "Message{" +
				"type=" + type +
				", text='" + text + '\'' +
				", nickname='" + nickname + '\'' +
				'}';
	}
}
