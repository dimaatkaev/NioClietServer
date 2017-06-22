package com.server;

import java.nio.ByteBuffer;

public class ClientBuffers {
    private final ByteBuffer[] toRead;
    private final ByteBuffer[] toWrite;

    public ClientBuffers(ByteBuffer[] toRead, ByteBuffer[] toWrite) {
        this.toRead = toRead;
        this.toWrite = toWrite;
    }

    public ByteBuffer[] getToRead() {
        return toRead;
    }

    public ByteBuffer[] getToWrite() {
        return toWrite;
    }

    @Override
    public String toString() {
        return "Client buffers toRead: " + toRead.length + ", toWrite: " + toWrite.length;
    }
}
