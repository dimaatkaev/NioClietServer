package com;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessageUtils {

    private static final int BYTE_BUFFER_CAPACITY = 256;
    private static final byte TERMINATOR = 30;

    public static Message getMessage(SocketChannel client, ByteBuffer clientBuffer) throws IOException, ClassNotFoundException {
        List<Byte[]> messageAsList = new ArrayList<>();
        while (true) {
            client.read(clientBuffer);
            byte[] receivedBytes = getBytesFromBuffer(clientBuffer);
            Byte[] part = MessageUtils.toObjByte(receivedBytes);
            messageAsList.add(part);
            if (findTerminatorPosition(part) == -1) {
                clientBuffer.clear();
            } else {
                if (part[part.length - 1] != 30) {
                    logError("Could not find terminator in message.");
                }
                clientBuffer.clear();
                break;
            }
        }

        return MessageUtils.collectMessage(messageAsList);
    }

    public static void sendMessage(SocketChannel client, Message originMessage) throws IOException {
        List<Byte[]> message = MessageUtils.splitMessage(originMessage);

        for (Byte[] part : message) {
            ByteBuffer communicationBuffer = ByteBuffer.wrap(MessageUtils.toPrimByte(part));
            client.write(communicationBuffer);
            communicationBuffer.clear();
        }
    }

    private static byte[] getMessageAsByteArray(Message message) throws IOException {
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

        // make array length same as list before terminator
        int lastPartSize = findTerminatorPosition(originList.get(originList.size() - 1));

        Byte[] collectParts = new Byte[bufferSizeWithoutLastPart + lastPartSize];
        for (int i = 0; i < originList.size(); i++) {
            Byte[] array = originList.get(i);
            for (int j = 0; j < array.length; j++) {
                if (array[j] != TERMINATOR) {
                    collectParts[i * BYTE_BUFFER_CAPACITY + j] = array[j];
                } else {
                    break;
                }
            }
        }
        return collectParts;
    }

    public static List<Byte[]> splitMessage(Message message) throws IOException {
        Byte[] originArray = toObjByte(getMessageAsByteArray(message));
        return splitByteArrayToParts(originArray);
    }

    public static ByteBuffer[] splitToByteBuffer(Message message) throws IOException {
        Byte[] originArray = toObjByte(getMessageAsByteArray(message));
        List<Byte[]> list = splitByteArrayToParts(originArray);
        ByteBuffer[] result = new ByteBuffer[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Byte[] objBytes = list.get(i);
            byte[] primBytes = toPrimByte(objBytes);
            result[i] = ByteBuffer.wrap(primBytes);
        }
        return result;
    }

    public static Message collectMessage(List<Byte[]> originList) throws IOException, ClassNotFoundException {
        byte[] collectedArray = toPrimByte(collectByteArrayByParts(originList));
        return getMessageFromByteArray(collectedArray);
    }

    public static Message collectMessageFromPrim(List<byte[]> originList) throws IOException, ClassNotFoundException {
        List<Byte[]> objByteArrayList = listToObjArrayByte(originList);
        byte[] collectedArray = toPrimByte(collectByteArrayByParts(objByteArrayList));
        return getMessageFromByteArray(collectedArray);
    }

    public static Byte[] toObjByte(byte[] primBytes) {
        Byte[] bytes = new Byte[primBytes.length];
        Arrays.setAll(bytes, n -> primBytes[n]);
        return bytes;
    }

    public static List<Byte[]> listToObjArrayByte(List<byte[]> primBytes) {
        return primBytes.stream().map(bytes -> toObjByte(bytes)).collect(Collectors.toList());
    }

    public static byte[] getBytesFromBuffer(ByteBuffer buf) {
        buf.flip();  //make buffer ready for read
        byte[] currentBytes = Arrays.copyOf(buf.array(), buf.limit());
        buf.clear(); //make buffer ready for writing
        return currentBytes;
    }

    public static byte[] toPrimByte(Byte[] objBytes) {
        byte[] bytes = new byte[objBytes.length];
        for (int i = 0; i < objBytes.length; i++) {
            bytes[i] = objBytes[i];
        }
        return bytes;
    }

    private static void logError(String logline) {
        System.out.println(MessageUtils.class.getName() + " ERROR: " + logline + ".");
    }

    /**
     * Find terminator in the byte array.
     *
     * @param array
     * @return -1 if could not find
     */
    public static int findTerminatorPosition(Byte[] array) {
        String str = new String(toPrimByte(array));
        return str.indexOf(TERMINATOR);
    }

    /**
     * Find terminator in the byte array.
     *
     * @param array
     * @return -1 if could not find
     */
    public static int findTerminatorPosition(byte[] array) {
        String str = new String(array);
        return str.indexOf(TERMINATOR);
    }
}
