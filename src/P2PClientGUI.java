import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class P2PClientGUI extends Component {
    /**
     * Labels
     **/
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

    /**
     * Text Fields/Areas
     **/
    private JTextField serverHostname;
    private JTextField port;
    private JTextField username;
    private JTextField hostname;
    private JTextField keyword;
    private JTextField command;

    private JTextArea commandLineArea;

    /**
     * Buttons
     **/
    private JButton connectButton;
    private JButton searchButton;
    private JButton goButton;
    private JButton refreshButton;


    /**
     * Combo Boxes
     **/
    private JComboBox<String> speedBox;


    /**
     * Everything else
     **/
    private JTable hostsTable;
    private JPanel mainPanel;
    private JScrollPane commandPane;
    private JScrollPane tablePane;

    /**
     * Instances
     **/
    public P2PClient client;
    private DefaultTableModel model;

    public P2PClientGUI() {
        JFrame frame = new JFrame("P2P Client");
        speedBox.addItem("T1");
        speedBox.addItem("T3");
        speedBox.addItem("Ethernet");
        speedBox.addItem("Modem");

        try {
            hostname.setText(InetAddress.getLocalHost().getHostName() + "/" + InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //setting defaults because I'm lazy
        port.setText("8081");
        serverHostname.setText("35.40.125.251");
        username.setText("user");

        ButtonListener buttonListener = new ButtonListener();
        searchButton.addActionListener(buttonListener);
        connectButton.addActionListener(buttonListener);
        goButton.addActionListener(buttonListener);
        refreshButton.addActionListener(buttonListener);
        commandLineArea.setVisible(true);
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
        hostsTable.setAutoCreateRowSorter(true);
    }

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            //need to wait for client to give table
            if (e.getSource() == searchButton) {
                Set<String> peerSet = null;

                while (peerSet == null) {
                    int sizeCurr = 0;
                    if (peerSet != null) {
                        sizeCurr = peerSet.size();
                    }
                    client.searchCommand = keyword.getText();
                    client.sendSearchCommand(client.searchCommand);
                    //might need some fixing

                    while (peerSet == null) {
                        client.checkForPeers();
                        peerSet = client.loadPeerInfo();
                    }
                    if (peerSet.size() <= sizeCurr) {
                        break;
                    }
                    System.out.println("Found " + peerSet.size() + " files");
                    for (String s : peerSet) {
                        String[] data = s.split(":");
                        model.addRow(data);
                    }

                    client.peerSet.clear();

                    //model.fireTableDataChanged();
                    hostsTable.setModel(model);
                }
            }

            //Handle open button action.
//            if (e.getSource() == findMyFilesButton) {
//                JFileChooser fc = new JFileChooser(System.getProperty("user.home") + "\\IdeaProjects\\CIS457-Project-2-P2P");
//                fc.setDialogTitle("Multiple file selection:");
//                fc.setMultiSelectionEnabled(true);
//                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//
//                int returnValue = fc.showOpenDialog(null);
//                if (returnValue == JFileChooser.APPROVE_OPTION) {
//                    File[] files = fc.getSelectedFiles();
//                    Arrays.asList(files).forEach(x -> {
//                        if (x.isFile()) {
//                            fileNamesBox.addItem(x.getName());
//                        }
//                    });
//                }
//            }
//            // connects
//
//            if (e.getSource() == sendFileButton) {
//                System.out.println("Sending new file...");
//                client.newFileCommand = Objects.requireNonNull(fileNamesBox.getSelectedItem()).toString() + " " + description.getText();
//                try {
//                    client.sendNewFileCommand(client.newFileCommand);
//                } catch (Exception ex) {
//                    System.out.println("No files found.");
//                }
//            }

            if (e.getSource() == connectButton) {
                if (connectButton.getText().equals("Connect")) {
                    connectButton.setText("Disconnect");
                    String connect = username.getText() + " " + hostname.getText() + " "
                            + Objects.requireNonNull(speedBox.getSelectedItem()).toString() + System.lineSeparator();

                    client = new P2PClient(serverHostname.getText(), Integer.parseInt(port.getText()));
                    client.connectCommand = connect;
                    client.start();
                } else {
                    connectButton.setText("Connect");
                    client.disconnectCommand = "quit: ";
                    try {
                        client.sendDisconnectCommand(client.disconnectCommand);
                        client.socket.close();
                        client = null;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            //need to wait for client to give command line
            if (e.getSource() == goButton) {
                String comm = command.getText();
                commandLineArea.append(">> " + comm + "\n");
                try {
                    client.sendFTPCommand(comm);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                commandLineArea.append(client.commandline);
            }

            if (e.getSource() == refreshButton) {
                //might need some fixing
                model.setRowCount(0);
                Set<String> peerSet = null;

                while (peerSet == null) {
                    int sizeCurr = 0;
                    if (peerSet != null) {
                        sizeCurr = peerSet.size();
                    }
                    client.searchCommand = keyword.getText();
                    client.sendSearchCommand(client.searchCommand);
                    //might need some fixing

                    while (peerSet == null) {
                        client.checkForPeers();
                        peerSet = client.loadPeerInfo();
                    }
                    if (peerSet.size() <= sizeCurr) {
                        break;
                    }
                    System.out.println("Found " + peerSet.size() + " files");
                    for (String s : peerSet) {
                        String[] data = s.split(":");
                        model.addRow(data);
                    }

                    client.peerSet.clear();
                    model.fireTableDataChanged();
                    hostsTable.setModel(model);
                }
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
