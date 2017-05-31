package com.clientGUI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class ClientInterface {
    JTextField outgoing;
    Manager manager = Manager.getInstance();

    public void go() throws InterruptedException {
        JFrame frame = new JFrame("Ludicrously Simple Chat Client");
        JPanel mainPanel = new JPanel();
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        setUpNetworking();
        frame.setSize(400, 500);
        frame.setVisible(true);

    }

    private void setUpNetworking() throws InterruptedException {
        try {
            askNickname();
            do {
                Thread.sleep(500);
                System.out.println("sleeping period was 500.");
            } while (manager.getNickname() == null);

            manager.sendRegisterMessage();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            try {
                String text = outgoing.getText();
                manager.sendCommunicationMessage("stub", text);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ClientInterface().go();
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
