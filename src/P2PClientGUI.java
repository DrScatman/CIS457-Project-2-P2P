import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;
import java.util.Scanner;

public class P2PClientGUI {
    /** Labels **/
    private JLabel serverHostnameLabel;
    private JLabel connectionLabel;
    private JLabel portLabel;
    private JLabel usernameLabel;
    private JLabel hostnameLabel;
    private JLabel speedLabel;
    private JLabel searchLabel;
    private JLabel keywordLabel;
    private JLabel ftpLabel;
    private JLabel commandLabel;

    /** Textfields **/
    private JTextField serverHostname;
    private JTextField port;
    private JTextField username;
    private JTextField hostname;
    private JTextField keyword;
    private JTextField command;

    /** Buttons **/
    private JButton connectButton;
    private JButton searchButton;
    private JButton goButton;

    /** Everything else **/
    private JComboBox<String> speedBox;
    private JTable hostsTable;
    private JTextArea commandLineArea;
    private JPanel mainPanel;
    private JScrollPane commandPane;
    private JScrollPane tablePane;
    private JButton refreshButton;

    /** Instances **/
    public  P2PClient client;
    private DefaultTableModel model;

    private P2PClientGUI() {
        JFrame frame = new JFrame("P2P Client");
        speedBox.addItem("T1");
        speedBox.addItem("T3");
        speedBox.addItem("Ethernet");
        speedBox.addItem("Modem");

        try {
            hostname.setText(InetAddress.getLocalHost().getHostName() + "/" + InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e){
            System.out.println(e);
        }

        //setting defaults because I'm lazy
        port.setText("8080");
        serverHostname.setText("localhost");
        username.setText("user");

        ButtonListener buttonListener = new ButtonListener();
        searchButton.addActionListener(buttonListener);
        connectButton.addActionListener(buttonListener);
        goButton.addActionListener(buttonListener);

        refreshButton.addActionListener(buttonListener);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("P2PClientGUI");
        frame.setContentPane(new P2PClientGUI().mainPanel);
        WindowListener windowListener = new WindowListener();

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(windowListener);
        frame.pack();
        frame.setVisible(true);
        //constantly update table and panel

    }

    private void createUIComponents() {
        model = new DefaultTableModel();
        model.addColumn("Speed");
        model.addColumn("Hostname");
        model.addColumn("Filename");
        hostsTable = new JTable(model);
    }

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            //need to wait for client to give table
            if (e.getSource() == searchButton){
                //client.sendSearchCommand(word);
                client.searchCommand = keyword.getText();
                System.out.println(client.searchCommand);
                //might need some fixing
//                ArrayList<Peer> peers = null;
//                while (peers == null){
//                    peers = client.loadPeerList();
//                }
//                for (Peer peer : peers) {
//                    String[] tableRow = new String[] {
//                            peer.getSpeed(), peer.getHostName(), peer.getHostName()
//                    };
//                    model.addRow(tableRow);
//                }
            }

            if (e.getSource() == connectButton) {
                String connect = username.getText() + " " + hostname.getText() + " "
                        + Objects.requireNonNull(speedBox.getSelectedItem()).toString() + System.lineSeparator();

                client = new P2PClient(serverHostname.getText(), Integer.parseInt(port.getText()));
                client.connectCommand = connect;
                client.start();
            }

            //need to wait for client to give command line
            if (e.getSource() == goButton){
                String comm = command.getText();
                //client.sendFTPCommand(comm);
                //might need some fixing
                Scanner reader = new Scanner(client.sendCommandLine());
                while (reader.hasNext()){
                    command.setText(reader.next());
                }
            }

            if (e.getSource() == refreshButton){
                String word = keyword.getText();
                client.sendSearchCommand(word);
                //might need some fixing
                model.setRowCount(0);
                ArrayList<Peer> peers = null;
                while (peers == null){
                    peers = client.loadPeerList();
                }
                for (Peer peer : peers) {
                    String[] tableRow = new String[] {
                            peer.getSpeed(), peer.getHostName(), peer.getHostName()
                    };
                    model.addRow(tableRow);
                }
                model.fireTableDataChanged();
                hostsTable.setModel(model);
            }
        }
    }

    public static class WindowListener extends WindowAdapter {

        //Figure out how to close the socket connection when closing GUI
        @Override
        public void windowClosing(WindowEvent e) {
            System.out.println("Closing");

            e.getWindow().dispose();
        }
    }
}
