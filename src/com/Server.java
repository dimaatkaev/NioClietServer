package com;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

	private MessageRouter messageRouter = new MessageRouter();

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		new Server().serverAction();
	}

	private static void log(String str) {
		System.out.println(str);
	}

	private void serverAction() throws IOException, ClassNotFoundException {
		// Selector: multiplexor of SelectableChannel objects
		Selector selector = Selector.open(); // selector is open here

		// ServerSocketChannel: selectable channel for stream-oriented listening sockets
		ServerSocketChannel serverSocket = ServerSocketChannel.open();
		InetSocketAddress serverAddr = new InetSocketAddress("localhost", 1111);

		// Binds the channel's socket to a local address and configures the socket to listen for connections
		serverSocket.bind(serverAddr);

		// Adjusts this channel's blocking mode.
		serverSocket.configureBlocking(false);

		int ops = serverSocket.validOps();
		SelectionKey selectKy = serverSocket.register(selector, ops, null);

		log("Start message router");
		messageRouter.start();


		// Infinite loop..
		// Keep server running
		while (true) {

			log("i'm a server and i'm waiting for new connection and buffer select...");
			// Selects a set of keys whose corresponding channels are ready for I/O operations
			selector.select();

			// token representing the registration of a SelectableChannel with a Selector
			Set<SelectionKey> clientKeys = selector.selectedKeys();
			Iterator<SelectionKey> keysIterator = clientKeys.iterator();


			while (keysIterator.hasNext()) {
				SelectionKey myKey = keysIterator.next();

				// Tests whether this key's channel is ready to accept a new socket connection
				if (myKey.isAcceptable()) {
					SocketChannel client = serverSocket.accept();

					// Adjusts this channel's blocking mode to false
					client.configureBlocking(false);

					// Operation-set bit for read operations
					client.register(selector, SelectionKey.OP_READ); //так же на чтение
					log("Connection Accepted: " + client.getLocalAddress() + "\n");
				} else if (myKey.isReadable()) {
					SocketChannel readSocketChannel = (SocketChannel) myKey.channel();
					ByteBuffer clientBuffer = ByteBuffer.allocate(256);
					readSocketChannel.read(clientBuffer);
					Message message = Message.getMessageFromByteArray(clientBuffer.array());

					message.setSocketChannel(readSocketChannel);
					messageRouter.addMessageInQueue(message);
					readSocketChannel.register(selector, SelectionKey.OP_WRITE); //так же на чтение
				} else if (myKey.isWritable()) {
					//TODO проверка есть ли что нибудь для записи
					SocketChannel client1 = (SocketChannel) myKey.channel();
					client1.register(selector, SelectionKey.OP_READ); //так же на чтение

				}
				keysIterator.remove();
			}
		}

	}
}
