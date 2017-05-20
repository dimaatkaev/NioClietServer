package com;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageUtils {

    private static final int BYTE_BUFFER_CAPACITY = 2;
    private static final byte TERMINATOR = 30;

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

    public static List<Byte[]> splitByteArrayToParts(Byte[] originArray) throws IOException {
        List<Byte[]> res = new ArrayList<>();

        List<Byte> part = new ArrayList<>();
        for (int i = 0; i < originArray.length; i++) {
            Byte b = originArray[i];
            addByte(res, part, b);
        }

        // add last part
        addByte(res, part, TERMINATOR);
        res.add(part.toArray(new Byte[part.size()]));
        return res;
    }

    private static void addByte(List<Byte[]> res, List<Byte> part, byte b) {
        if (part.size() == BYTE_BUFFER_CAPACITY) {
            res.add(part.toArray(new Byte[part.size()]));
            part.clear();
            part.add(b);
        } else {
            part.add(b);
        }
    }

    public static Byte[] collectByteArrayByParts(List<Byte[]> originList) {
        int bufferSizeWithoutLastPart = (originList.size() - 1) * BYTE_BUFFER_CAPACITY;
        // - 1 means reduce size because no need to collect terminator
        int lastPartSize = originList.get(originList.size() - 1).length - 1;

        Byte[] collectParts = new Byte[bufferSizeWithoutLastPart + lastPartSize];
        for (int i = 0; i < originList.size(); i++) {
            Byte[] array = originList.get(i);
            for (int j = 0; j < array.length; j++) {
                if (array[j] != TERMINATOR) {
                    collectParts[i * BYTE_BUFFER_CAPACITY + j] = array[j];
                } else {
                    if (array[j] == TERMINATOR && array.length - 1 != j){
                        log("Terminator found before finish read bytes.");
                    }
                }
            }
        }
        return collectParts;
    }

    public static List<Byte[]> splitMessage(Message message) throws IOException {
        Byte[] originArray = toObjByte(getMessageAsByteArray(message));
        return splitByteArrayToParts(originArray);
    }

    public static Message collectMessage(List<Byte[]> originList) throws IOException, ClassNotFoundException {
        byte[] collectedArray = toPrimByte(collectByteArrayByParts(originList));
        return getMessageFromByteArray(collectedArray);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("Arrays test:");
        Byte[] origin = {1, 2, 3, 4, 5, 6};
        List<Byte[]> list = splitByteArrayToParts(origin);
        System.out.println("Splited: ");
        for (Byte[] b : list) {
            System.out.println(Arrays.toString(b));
        }

        Byte[] res = collectByteArrayByParts(list);
        System.out.println("Result:");
        System.out.println(Arrays.toString(res));

        System.out.println("Message test:");
        Message m = new Message(Message.Type.COMMUNICATION, "hi", "owner");
        List<Byte[]> originList = splitMessage(m);
        System.out.println("List size = " + originList.size());
        System.out.println(collectMessage(originList));
    }

    private static Byte[] toObjByte(byte[] primBytes) {
        Byte[] bytes = new Byte[primBytes.length];
        Arrays.setAll(bytes, n -> primBytes[n]);
        return bytes;
    }

    private static byte[] toPrimByte(Byte[] objBytes) {
        byte[] bytes = new byte[objBytes.length];
        for (int i = 0; i < objBytes.length; i++) {
            bytes[i] = objBytes[i];
        }
        return bytes;
    }

    private static void log(String logline) {
        System.out.println(MessageUtils.class.getName() + " ERROR [" + logline + "]");
    }
}
