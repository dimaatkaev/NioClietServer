package com.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ClientBuffers {
    private ByteBuffer[] toRead;
    private ByteBuffer[] toWrite;

    private List<byte[]> collectedBytes;

    public ClientBuffers(ByteBuffer[] toRead, ByteBuffer[] toWrite) {
        this.toRead = toRead;
        this.toWrite = toWrite;
        collectedBytes = new ArrayList<>();
    }

    public void cleanUp() {
        collectedBytes.clear();
    }

    public List<byte[]> getCollectedBytes() {
        return collectedBytes;
    }

    public ByteBuffer[] getToRead() {
        return toRead;
    }

    public ByteBuffer[] getToWrite() {
        return toWrite;
    }

    public void setToRead(ByteBuffer[] toRead) {
        this.toRead = toRead;
    }

    public void setToWrite(ByteBuffer[] toWrite) {
        this.toWrite = toWrite;
    }

    @Override
    public String toString() {
        return "Client buffers toRead: " + toRead.length + ", toWrite: " + toWrite.length;
    }
}
