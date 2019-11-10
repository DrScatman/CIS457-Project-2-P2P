import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class P2PClient extends Thread {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean connectedToCentralServer;
    String searchCommand;
    private String searchResponse;
    private String FTPCommand;
    String connectCommand;
    private FTPServer ftpServer;
//
//    public String getConnectCommand() {
//        return connectCommand;
//    }

    private FTPClient ftpClient;
    private static final String FILE_LIST_FILENAME = "filelist.txt";

    public P2PClient(String serverHostName, int port) {
        try {
            socket = new Socket(serverHostName, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

//            ftpServer = new FTPServer();
//            ftpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {

                if (connectCommand != null && !connectCommand.isEmpty()) {
                    sendConnectCommand(connectCommand);
                    System.out.println("Connect command sent to:  " + socket.getInetAddress().getHostAddress());
                    connectCommand = null;
                    // && in.readUTF().toLowerCase().contains("connect") :: taken out  else if from below
                }

                if (!connectedToCentralServer) {
                    // Sends file list file to server
                    //ftpClient.sendCommand("stor: " + FILE_LIST_FILENAME);

                    File folder = new File("C:\\Users\\mille\\eclipse\\java-2018-092\\eclipse");
                    File[] listOfFiles = folder.listFiles();
                    StringBuilder listOfFileNames = new StringBuilder();

                    try {
                        if (listOfFiles.length > 0 ) {
                            listOfFileNames.append("200 ");

                            for (File file : listOfFiles) {
                                if (file.isFile()) {
                                    listOfFileNames.append(file.getName()).append(" ");
                                }
                            }
                        }
                        else {
                            listOfFileNames.append("404");
                        }

                        listOfFileNames.append("\r\n");
                        out.writeBytes(listOfFileNames.toString());

                    System.out.println("Files sent to:  " + socket.getInetAddress().getHostAddress());
                    }
                    catch (NullPointerException e) {
                        System.out.println("No files found.");
                    }

                    connectedToCentralServer = true;
                }

                if (FTPCommand != null && !FTPCommand.isEmpty()) {
                    System.out.println("Sending: " + FTPCommand + "To Central Server");
                    sendFTPCommand(FTPCommand);
                    FTPCommand = null;
                }

                if(searchCommand != null && !searchCommand.isEmpty() ) {
                    System.out.println("Searching for: " + searchCommand);
                    sendSearchCommand(searchCommand);
                    searchCommand = null;
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
        connectCommand = command;
        out.writeBytes(connectCommand);
    }

    // Needs to send to CentralServer somewhere
    public void sendFTPCommand(String command) throws IOException {
        FTPCommand = command;
        out.writeBytes(FTPCommand);
    }

    public ArrayList<Peer> loadPeerList() {
        return null;
    }

    public String sendCommandLine() {
        return null;
    }

    public void sendSearchCommand(String command) {
        try {
            searchCommand = "search " + command;
            out.writeBytes(searchCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//        DataOutputStream dataToServer = new DataOutputStream(dataSocket.getOutputStream());
//        dataToServer.writeBytes(searchCommand);

    public void loadSearchResults() {
        /*BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
        searchResponse = inData.readLine();*/
    }
}
