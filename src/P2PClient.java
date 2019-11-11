import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class P2PClient extends Thread {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    //    private boolean connectedToCentralServer;
    private String searchResponse;
    private FTPServer ftpServer;
    private ObjectInputStream ois;
    private HashSet<Peer> peerSet;
    private boolean searchCommandSent;
    private static final String FILE_LIST_FILENAME = "filelist.txt";
    private FTPClient ftpClient;

    /**
     * Commands that are sent to the client handler
     **/
    String connectCommand;
    String searchCommand;
    private String FTPCommand;
    String newFileCommand;


    public P2PClient(String serverHostName, int port) {
        try {
            socket = new Socket(serverHostName, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            //ois = new ObjectInputStream(socket.getInputStream());

            ftpServer = new FTPServer();
            ftpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {

                if (connectCommand != null && !connectCommand.isEmpty()) {
                    sendConnectCommand(connectCommand);
                    System.out.println("Connect command sent to:  " + socket.getInetAddress().getHostAddress());
                    connectCommand = null;
                }

                if (newFileCommand != null && !newFileCommand.isEmpty()) {
                    newFileCommand = null;
                }

                if (FTPCommand != null && !FTPCommand.isEmpty()) {
                    System.out.println("Sending: " + FTPCommand + "To Central Server");
                    sendFTPCommand(FTPCommand);
                    FTPCommand = null;
                }

                if (searchCommand != null && !searchCommand.isEmpty()) {
                    System.out.println("Searching for: " + searchCommand);
                    sendSearchCommand(searchCommand);
                    searchCommandSent = true;
                    searchCommand = null;
                }

                if (searchCommandSent) {
                    int len = in.readByte();
                    while (len > 0) {
                        try {
                            Peer peer = (Peer) ois.readObject();
                            if (peer != null) {
                                peerSet.add(peer);
                            }
                        } catch (ClassNotFoundException ignored) {
                        }
                    }
                    searchCommandSent = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //needs to get list of clients and their files from the server
    //needs to have FTP client and server implemented, so detects connection and chooses which to be
    //always listening on port 8080 after connecting to server
    //has to send list of clients, and their files, and commands to GUI
    //has to receive commands from the gui for connecting to server and other peers
    //needs to upload file list and descriptions

    // Needs to send to CentralServer somewhere
    public void sendConnectCommand(String command) throws IOException {
        connectCommand = "newPeer " + command + "\r\n";
        out.writeBytes(connectCommand);
    }

    // Needs to send to CentralServer somewhere
    public void sendFTPCommand(String command) throws IOException {
        FTPCommand = command;
        out.writeBytes(FTPCommand);
    }

    public HashSet<Peer> loadPeerList() {
        return peerSet;
    }

    public String sendCommandLine() {
        return null;
    }

    public void sendSearchCommand(String command) {
        try {
            searchCommand = "search " + command + "\r\n";
            out.writeBytes(searchCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNewFileCommand(String command) throws IOException {
        newFileCommand = "200 " + command + "\r\n";
        out.writeBytes(newFileCommand);
        System.out.println("Files sent to:  " + socket.getInetAddress().getHostAddress());
    }

    public void loadSearchResults() {
        /*BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
        searchResponse = inData.readLine();*/
    }
}
