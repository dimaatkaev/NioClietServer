package com.handlers;

import com.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class RegisterHandler implements Handler, Runnable {
	private Map<String, SocketChannel> clients;
	Message message;

	@Override
	public void setMessage(Message message) {
		this.message = message;
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
		clients.put(message.getNickname(), message.getSocketChannel());
		log("new participant: " + message.getNickname());


		try {
			String clientsAsString = String.join(", ", clients.keySet());
			Message request = new Message(Message.Type.REGISTER_REQUEST, clientsAsString, "server");
			byte[] requestBytes = Message.getMessageAsByteArray(request);
			SocketChannel socketChannel = clients.get(message.getNickname());
			ByteBuffer communicationBuffer = ByteBuffer.wrap(requestBytes);
			boolean sent = true;
			while (sent) {
				if (socketChannel.isConnected()) {
					socketChannel.write(communicationBuffer);
					sent = false;
					log("sending register request message: " + request.toString() + " sent.");
				}
			}
			communicationBuffer.clear();
		} catch (IOException e) {
			log("!!!ERROR TASK EXE!!!");
			e.printStackTrace();
		}


	}

	private static void log(String str) {
		System.out.println(str);
	}
}
