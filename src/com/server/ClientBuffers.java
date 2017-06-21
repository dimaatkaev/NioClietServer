package com.server;

import java.nio.ByteBuffer;

public class ClientBuffers {
    private ByteBuffer[] toRead;
    private ByteBuffer[] toWrite;

    public ClientBuffers(ByteBuffer[] toRead, ByteBuffer[] toWrite) {
        this.toRead = toRead;
        this.toWrite = toWrite;
    }

    public ByteBuffer[] getToRead() {
        return toRead;
    }

    public void setToRead(ByteBuffer[] toRead) {
        this.toRead = toRead;
    }

    public ByteBuffer[] getToWrite() {
        return toWrite;
    }

    public void setToWrite(ByteBuffer[] toWrite) {
        this.toWrite = toWrite;
    }
}
