package com.dima.clientGUI;

import com.dima.messaging.Message;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import static javax.swing.JFrame.EXIT_ON_CLOSE;


public class ClientInterface {
    JTextField outgoing;
    JList participantsList;
    JTextArea incoming;
    Manager manager = Manager.getInstance();
    List<String> participants = manager.getParticipants();
    List<Message> incomingMessages = manager.getIncomingMessages();

    int communicationMessagesCount = 0;
    int participantsCount = 0;

    public static void main(String[] args) throws InterruptedException {
        new ClientInterface().go();
    }

    public void go() throws InterruptedException {
        JFrame frame = new JFrame("Ludicrously Simple Chat Client");
        JPanel mainPanel = new JPanel();

        // staff
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        participantsList = new JList(manager.getParticipants().toArray());
        participantsList.setSelectedIndex(0);
        participantsList.setFixedCellWidth(200);

        incoming = new JTextArea(15, 20);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        // put stuff on panel
        mainPanel.add(qScroller);
        mainPanel.add(new JScrollPane(participantsList), BorderLayout.WEST);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        setUpNetworking();

        // run listener
        new UpdateLookUp().start();

        frame.setSize(400, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setUpNetworking() throws InterruptedException {
        try {
            askNickname();
            do {
                Thread.sleep(500);
                System.out.println("sleeping period was 500.");
            } while (manager.getSender() == null);

            manager.sendRegisterMessage();

            // wait for response
            Thread.sleep(1000);
            participantsList.setListData(manager.getParticipants().toArray());
            participantsCount = manager.getParticipants().size();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            try {
                String text = outgoing.getText();
                String recipient = (String) participantsList.getSelectedValue();
                manager.sendCommunicationMessage(recipient, text);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }

    private void askNickname() {
        JFrame askFrame = new JFrame("Enter your nickname");
        JPanel panel = new JPanel();
        JTextField nickNameField = new JTextField(20);
        JButton sendButton = new JButton("OK");

        sendButton.addActionListener(e -> {
            String nickName;
            nickName = nickNameField.getText();
            manager.setSender(nickName);
            askFrame.setVisible(false);
        });

        panel.add(nickNameField);
        panel.add(sendButton);
        askFrame.getContentPane().add(BorderLayout.CENTER, panel);

        askFrame.setSize(400, 100);
        askFrame.setVisible(true);
    }

    class UpdateLookUp extends Thread {


        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    if (participants.size() > participantsCount) {
                        participantsList.setListData(participants.toArray());
                        participantsCount = participants.size();
                    }

                    if (incomingMessages.size() > communicationMessagesCount) {
                        if (!incomingMessages.isEmpty()) {
                            incoming.append(incomingMessages.get(incomingMessages.size() - 1).getView() + "\n");
                            communicationMessagesCount++;
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
