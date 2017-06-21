package com.server;

import java.nio.ByteBuffer;

public class ClientBuffersFactory {
    private static final int BUFFER_CAPACITY = 256;

    public static ClientBuffers getClientBuffers() {
        ByteBuffer[] buffersToRead = {ByteBuffer.allocate(BUFFER_CAPACITY)};
        ByteBuffer[] buffersToWrite = {ByteBuffer.allocate(BUFFER_CAPACITY)};
        buffersToRead[0].clear();
        buffersToWrite[0].clear();
        return new ClientBuffers(buffersToRead, buffersToWrite);
    }
}
