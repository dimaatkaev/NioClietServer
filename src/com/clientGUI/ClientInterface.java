package com.clientGUI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import static javax.swing.JFrame.EXIT_ON_CLOSE;


public class ClientInterface {
    JTextField outgoing;
    JList participantsList;
    Manager manager = Manager.getInstance();

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
        mainPanel.add(new JScrollPane(participantsList), BorderLayout.WEST);

        // put stuff on panel
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        setUpNetworking();
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
            } while (manager.getNickname() == null);

            manager.sendRegisterMessage();

            // wait for response
            Thread.sleep(1000);
            participantsList.setListData(manager.getParticipants().toArray());
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
            manager.setNickname(nickName);
            askFrame.setVisible(false);
        });

        panel.add(nickNameField);
        panel.add(sendButton);
        askFrame.getContentPane().add(BorderLayout.CENTER, panel);

        askFrame.setSize(400, 100);
        askFrame.setVisible(true);
    }
}
