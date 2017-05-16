package com;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MessageUtils {

    public static byte[] getMessageAsByteArray(Message message) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bo);
        out.writeObject(message);
        return bo.toByteArray();
    }

    public static Message getMessageFromByteArray(byte[] array) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bi = new ByteArrayInputStream(array);
        ObjectInputStream in = new ObjectInputStream(bi);
        return (Message) in.readObject();
    }
}
