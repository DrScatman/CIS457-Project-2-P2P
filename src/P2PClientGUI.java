import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class P2PClientGUI {
    private JLabel connectionLabel;
    private JLabel serverHostnameLabel;
    private JTextField serverHostname;
    private JLabel portLabel;
    private JTextField port;
    private JButton connectButton;
    private JLabel usernameLabel;
    private JTextField username;
    private JLabel hostnameLabel;
    private JTextField hostname;
    private JLabel speedLabel;
    private JComboBox<String> speedBox;
    private JLabel searchLabel;
    private JLabel keywordLabel;
    private JTextField keyword;
    private JButton searchButton;
    private JTable hostsTable;
    private JLabel ftpLabel;
    private JLabel commandLabel;
    private JTextField command;
    private JButton goButton;
    private JTextArea commandLineArea;
    private JPanel mainPanel;
    private JScrollPane commandPane;
    private JScrollPane tablePane;
    private P2PClient client;
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
        ButtonListener buttonListener = new ButtonListener();
        searchButton.addActionListener(buttonListener);
        connectButton.addActionListener(buttonListener);
        goButton.addActionListener(buttonListener);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("P2PClientGUI");
        frame.setContentPane(new P2PClientGUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                String word = keyword.getText();
                client.sendSearchCommand(word);
                //might need some fixing
                ArrayList<Peer> peers = null;
                while (peers == null){
                    peers = client.loadPeerList();
                }
                for (Peer peer : peers){
                    String[] tableRow = new String[] {
                            peer.getSpeed(), peer.getHostName(), peer.getHostName()
                    };
                    model.addRow(tableRow);
                }
            }

            if (e.getSource() == connectButton) {
                String connect = username.getText() + " " + hostname.getText() + " "
                        + Objects.requireNonNull(speedBox.getSelectedItem()).toString() + System.lineSeparator();

                client = new P2PClient(serverHostname.getText(), Integer.parseInt(port.getText()));
                client.sendConnectCommand(connect);
            }

            //need to wait for client to give command line
            if (e.getSource() == goButton){
                String comm = command.getText();
                client.sendFTPCommand(comm);
                //might need some fixing
                Scanner reader = new Scanner(client.sendCommandLine());
                while (reader.hasNext()){
                    command.setText(reader.next());
                }
            }
        }
    }
}
