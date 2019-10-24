import javax.swing.*;
import javax.swing.table.TableColumn;

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

    private P2PClientGUI() {
        client = new P2PClient();
        JFrame frame = new JFrame("P2P Client");
        speedBox.addItem("T1");
        speedBox.addItem("T3");
        speedBox.addItem("Ethernet");
        speedBox.addItem("Modem");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("P2PClientGUI");
        frame.setContentPane(new P2PClientGUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        String[] columnNames = {"Speed", "Hostname", "Filename"};
        String[][] data = {};
        hostsTable = new JTable(data, columnNames);
    }
}
