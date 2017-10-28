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

        clientBuffers.setToWrite(buffersToWritePlusOne);
        return clientBuffers;
    }

    public static ClientBuffers removeOneBufferToWrite(ClientBuffers clientBuffers) {
        ByteBuffer[] current = clientBuffers.getToWrite();
        ByteBuffer[] shrinked = Arrays.copyOf(current, current.length - 1);
        clientBuffers.setToWrite(shrinked);
        return clientBuffers;
    }

//    public static ClientBuffers addOneBufferToRead(ClientBuffers clientBuffers) {
//        ByteBuffer[] bufferToRead = {ByteBuffer.allocate(BUFFER_CAPACITY)};
//        ByteBuffer[] buffersToReadPlusOne = Stream.concat(
//                Arrays.stream(clientBuffers.getToRead()),
//                Arrays.stream(bufferToRead)
//        ).toArray(ByteBuffer[]::new);
//
//        return new ClientBuffers(buffersToReadPlusOne, clientBuffers.getToWrite());
//    }
}
