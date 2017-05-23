package com;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.MessageUtils.collectByteArrayByParts;
import static com.MessageUtils.collectMessage;
import static com.MessageUtils.splitByteArrayToParts;
import static com.MessageUtils.splitMessage;

public class Test {
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

        // ======================================================================================

        System.out.println("Message test:");
        Message m = new Message(Message.Type.COMMUNICATION, "hi", "owner");
        List<Byte[]> originList = splitMessage(m);
        System.out.println("List size = " + originList.size());
        System.out.println(collectMessage(originList));

        System.out.println("Message test 1:");
        Message m1 = new Message(
                Message.Type.COMMUNICATION,
                "123123123123123123123123123qewqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqwe" +
                        "qweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqw",
                "owner"
        );
        List<Byte[]> originList1 = splitMessage(m1);
        System.out.println("List size = " + originList1.size());
        System.out.println(collectMessage(originList1));

        // ======================================================================================

//        System.out.println("Socket test:");
//        SocketChannel socketChannel = new SocketChannel() {
//        }

    }
}
