import javax.swing.*;

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
    private JComboBox speedBox;
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("P2PClientGUI");
        frame.setContentPane(new P2PClientGUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
