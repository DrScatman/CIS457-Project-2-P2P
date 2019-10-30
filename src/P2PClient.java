import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class P2PClient extends Thread {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String searchCommand, searchResponse, FTPCommand, connectCommand;

    public P2PClient(String serverHostName, int port) {
        try {
            socket = new Socket(serverHostName, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());


            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (connectCommand != null && !connectCommand.isEmpty()) {
                    out.writeUTF(connectCommand);
                    connectCommand = null;
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
    public void sendConnectCommand(String command) {
        connectCommand = command;
    }

    // Needs to send to CentralServer somewhere
    public void sendFTPCommand(String command) {
        FTPCommand = command;
    }

    public ArrayList<Peer> loadPeerList() {
        return PeerWrapper.getUserList();
    }

    public String sendCommandLine() {
        return null;
    }

    public void sendSearchCommand(String command) {

        /*searchCommand = command;
        DataOutputStream dataToServer = new DataOutputStream(dataSocket.getOutputStream());
        dataToServer.writeBytes(searchCommand);*/
    }

    public void loadSearchResults() {
        /*BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
        searchResponse = inData.readLine();*/
    }
}
