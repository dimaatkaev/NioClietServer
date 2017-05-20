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

        List<Byte> temp = new ArrayList<>();
        for (Byte b : originArray) {
            if (temp.size() == BYTE_BUFFER_CAPACITY) {
                // complete fill part, collect in the res
                res.add(temp.toArray(new Byte[temp.size()]));
                temp.clear();
                // do not forget about current byte, collect it in temp list
                temp.add(b);
            } else {
                temp.add(b);
            }
        }
        // add last part
        res.add(temp.toArray(new Byte[temp.size()]));
        return res;
    }

    public static Byte[] collectByteArrayByParts(List<Byte[]> originList) {
        int bufferSizeWithoutLastPart = (originList.size() - 1) * BYTE_BUFFER_CAPACITY;
        int lastPartSize = originList.get(originList.size() - 1).length;

        Byte[] collectParts = new Byte[bufferSizeWithoutLastPart + lastPartSize];
        for (int i = 0; i < originList.size(); i++) {
            Byte[] array = originList.get(i);
            for (int j = 0; j < array.length; j++) {
                collectParts[i * BYTE_BUFFER_CAPACITY + j] = array[j];
            }
        }
        return collectParts;
    }

    public static List<Byte[]> splitMessage(Message message) throws IOException {
        Byte[] originArray = toObjectsByte(getMessageAsByteArray(message));
        return splitByteArrayToParts(originArray);
    }

    public static Message collectMessage(List<Byte[]> originList) throws IOException, ClassNotFoundException {
        byte[] collectedArray = toPrimitivesByte(collectByteArrayByParts(originList));
        return getMessageFromByteArray(collectedArray);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Message m = new Message(Message.Type.COMMUNICATION, "hi", "owner");
        List<Byte[]> originList = splitMessage(m);
        System.out.println("List size = " + originList.size());
        System.out.println(collectMessage(originList));
//        Byte[] origin = {1, 2, 3, 4, 5, 6, 7};
//        List<Byte[]> list = splitByteArrayToParts(origin);
//        System.out.println("Splited: ");
//        for (Byte[] b : list) {
//            System.out.println(Arrays.toString(b));
//        }
//
//        Byte[] res = collectByteArrayByParts(list);
//        System.out.println("Result:");
//        System.out.println(Arrays.toString(res));

    }

    private static Byte[] toObjectsByte(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        Arrays.setAll(bytes, n -> bytesPrim[n]);
        return bytes;
    }

    private static byte[] toPrimitivesByte(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];
        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }
        return bytes;
    }
}
