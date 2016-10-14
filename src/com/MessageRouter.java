package com;

import com.handlers.CommunicationHandler;
import com.handlers.Handler;
import com.handlers.RegisterHandler;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageRouter extends Thread {
	private BlockingQueue handleMessages = new ArrayBlockingQueue<Message>(10);
	private Map<Message.Type, Handler> handlers = new HashMap<>();
	private Map<String, SocketChannel> clients = new HashMap<>();
	private ExecutorService executor = Executors.newFixedThreadPool(5);

	public MessageRouter() {
		initHandlers();
	}

	public void initHandlers() {
		handlers.put(Message.Type.REGISTER, new RegisterHandler(clients));
		handlers.put(Message.Type.COMMUNICATION, new CommunicationHandler(clients));
	}

	public void addMessageInQueue(Message message) {
		handleMessages.add(message);
	}

	@Override
	public void run() {
		while (true) {
			if (!handleMessages.isEmpty()) {
				Message message = (Message) handleMessages.poll();
				if (message.getType().equals(Message.Type.REGISTER)) {
					Handler handler = handlers.get(Message.Type.REGISTER);
					handler.setMessage(message);
					executor.execute(handler);
					log("Register message = " + message.toString() + "\n");
				} else if (message.getType().equals(Message.Type.COMMUNICATION)) {
					handlers.get(Message.Type.COMMUNICATION);
					Handler handler = handlers.get(Message.Type.COMMUNICATION);
					handler.setMessage(message);
					executor.execute(handler);
					log("Communication message processed = " + message.toString() + "\n");
				}
			}
		}
	}

	public void getMessage(Message message) {
		if (handlers.containsKey(message.getType())) {
			handleMessages.add(message);
		} else {
			log("unknown message type");
		}
	}

	private static void log(String str) {
		System.out.println(str);
	}
}
