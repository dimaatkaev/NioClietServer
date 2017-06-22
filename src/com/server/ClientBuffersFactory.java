package com.server;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Stream;

public class ClientBuffersFactory {
    private static final int BUFFER_CAPACITY = 256;

    public static ClientBuffers getClientBuffers() {
        ByteBuffer[] buffersToRead = {ByteBuffer.allocate(BUFFER_CAPACITY)};
        ByteBuffer[] buffersToWrite = {ByteBuffer.allocate(BUFFER_CAPACITY)};
        buffersToRead[0].clear();
        buffersToWrite[0].clear();
        return new ClientBuffers(buffersToRead, buffersToWrite);
    }

    public static ClientBuffers addOneBufferToWrite(ClientBuffers clientBuffers) {
        ByteBuffer[] bufferToWrite = {ByteBuffer.allocate(BUFFER_CAPACITY)};
        ByteBuffer[] buffersToWritePlusOne = Stream.concat(
                Arrays.stream(clientBuffers.getToWrite()),
                Arrays.stream(bufferToWrite)
        ).toArray(ByteBuffer[]::new);

        return new ClientBuffers(clientBuffers.getToRead(), buffersToWritePlusOne);
    }

    public static ClientBuffers addOneBufferToRead(ClientBuffers clientBuffers) {
        ByteBuffer[] bufferToRead = {ByteBuffer.allocate(BUFFER_CAPACITY)};
        ByteBuffer[] buffersToReadPlusOne = Stream.concat(
                Arrays.stream(clientBuffers.getToRead()),
                Arrays.stream(bufferToRead)
        ).toArray(ByteBuffer[]::new);

        return new ClientBuffers(buffersToReadPlusOne, clientBuffers.getToWrite());
    }

//    public static void main(String[] args) {
//        ClientBuffers cb = getClientBuffers();
//        System.out.println("Start: " + cb);
//        cb = addOneBufferToRead(cb);
//        System.out.println("Add to read: " + cb);
//        cb = addOneBufferToWrite(cb);
//        System.out.println("Add to write: " + cb);
//    }

//    private static ClientBuffers cleanUp (ClientBuffers clientBuffers) {
//        Arrays.stream(clientBuffers.getToWrite()).forEach(byteBuffer -> byteBuffer.clear());
//        Arrays.stream(clientBuffers.getToRead()).forEach(byteBuffer -> byteBuffer.clear());
//        return clientBuffers;
//    }
//
//    public static void main(String[] args) {
//        ClientBuffers cb = getClientBuffers();
//        System.out.print("new: ");
//        System.out.println(cb.getToWrite()[0].array()[0]);
//        System.out.print("put one: ");
//        cb.getToWrite()[0].put(0, (byte)9);
//        System.out.println(cb.getToWrite()[0].array()[0]);
//        System.out.print("after cleaning: ");
//        System.out.println(cleanUp(cb).getToWrite()[0].array()[0]);
//    }
}
