package com.handlers;

import com.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class CommunicationHandler implements Handler, Runnable {
	private Map<String, SocketChannel> clients;
	private Message message;

	public CommunicationHandler(Map<String, SocketChannel> clients) {
		this.clients = clients;
	}

	@Override
	public void setMessage(Message message) {
		this.message = message;
	}

	@Override
	public void run() {
		processMessage();
	}

	@Override
	public void processMessage() {
		try {
            log("COMMUNICATION MESSAGE: " + message);
			byte[] communicationMessage = Message.getMessageAsByteArray(message);
			SocketChannel socketChannel = clients.get(message.getNickname());

			ByteBuffer communicationBuffer = ByteBuffer.wrap(communicationMessage);
			boolean sended = true;
			while (sended) {
				if (socketChannel.isConnected()) {
					socketChannel.write(communicationBuffer);
					sended = false;
					log("sending communication message: " + message.toString() + " sent.");
				}
			}
			communicationBuffer.clear();
		} catch (IOException e) {
			log("!!!ERROR COMMUNICATION TASK EXE !!!");
			e.printStackTrace();
		}
	}

	private static void log(String str) {
		System.out.println(str);
	}
}
