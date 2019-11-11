import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.File;
import java.net.InetAddress;
import java.util.*;
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class P2PClientGUI extends Component {
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
    private JLabel fileInfoLabel;
    private JLabel fileNamesLabel;
    private JLabel descriptionLabel;

    /** Textfields **/
    private JTextField serverHostname;
    private JTextField port;
    private JTextField username;
    private JTextField hostname;
    private JTextField keyword;
    private JTextField command;
    private JTextField description;

    /** Buttons **/
    private JButton connectButton;
    private JButton searchButton;
    private JButton goButton;

    /** Everything else **/
    private JComboBox<String> speedBox;
    private JComboBox<String> fileNamesBox;
    private JTable hostsTable;
    private JTextArea commandLineArea;
    private JPanel mainPanel;
    private JScrollPane commandPane;
    private JScrollPane tablePane;
    private JButton refreshButton;
    private JButton findMyFilesButton;

    /** Instances **/
    public  P2PClient client;
    private DefaultTableModel model;

    public P2PClientGUI() {
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
        port.setText("8081");
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
                //might need some fixing
                HashSet<Peer> peerSet = null;
                while (peerSet == null){
                    peerSet = client.loadPeerList();
                }
                for (Peer peer : peerSet) {
                    String[] tableRow = new String[] {
                            peer.getSpeed(), peer.getHostName(), peer.getHostName()
                            // did you mean to put this?:
                            //peer.getSpeed(), peer.getHostUserName(), peer.getHostName()
                    };
                    model.addRow(tableRow);
                }
            }

            //Handle open button action.
            if (e.getSource() == findMyFilesButton) {
                JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                fc.setDialogTitle("Multiple file selection:");
                fc.setMultiSelectionEnabled(true);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int returnValue = fc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File[] files = fc.getSelectedFiles();
                    Arrays.asList(files).forEach(x -> {
                        if (x.isFile()) {
                            fileNamesBox.addItem(x.getName());
                        }
                    });
                }
            }

            if (e.getSource() == connectButton) {
                if (connectButton.getText().equals("Connect")) {
                    connectButton.setText("Disconnect");
                } else {
                    connectButton.setText("Connect");
                }
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
                HashSet<Peer> peerSet = null;
                peerSet = client.loadPeerList();

                for (Peer peer : peerSet) {
                    String[] tableRow = new String[] {
                            peer.getSpeed(), peer.getHostName(), peer.getHostName()
                            // did you mean to put this?:
                            //peer.getSpeed(), peer.getHostUserName(), peer.getHostName()
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
