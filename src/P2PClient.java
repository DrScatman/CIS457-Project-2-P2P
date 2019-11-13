import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class P2PClient extends Thread {

    Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    //    private boolean connectedToCentralServer;
    private String searchResponse;
    private FTPServer ftpServer;
    public HashSet<String> peerSet;
    private boolean searchCommandSent;
    private static final String FILE_LIST_FILENAME = "filelist.txt";
    private FTPClient ftpClient;

    /**
     * Commands that are sent to the client handler
     **/
    String connectCommand;
    String disconnectCommand;
    String searchCommand;
    private String FTPCommand;
    String newFileCommand;
    String commandline;


    public P2PClient(String serverHostName, int port) {
        try {
            socket = new Socket(serverHostName, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            peerSet = new HashSet<>();

            ftpServer = new FTPServer();
            ftpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
//        ftpServer.run();
        while (socket.isConnected() && !socket.isClosed()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                if (connectCommand != null && !connectCommand.isEmpty()) {
                    sendConnectCommand(connectCommand);
                    System.out.println("Connect command sent to:  " + socket.getInetAddress().getHostAddress());
                    connectCommand = null;
                    sendFileList();

                    sendSearchCommand(" ");
                    checkForPeers();
                    loadPeerInfo();
                }

                if (disconnectCommand != null && !disconnectCommand.isEmpty()) {
                    disconnectCommand = null;
                }

                if (newFileCommand != null && !newFileCommand.isEmpty()) {
                    newFileCommand = null;
                }
                ////FIX this  should go to FTP server of peer who has a file you want
                if (FTPCommand != null && !FTPCommand.isEmpty()) {
                    System.out.println("Sending: " + FTPCommand + "To Central Server");
                    sendFTPCommand(FTPCommand);
                    FTPCommand = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Disconnected");
    }

    private void sendFileList() {
        String path = System.getProperty("user.home") + "\\IdeaProjects\\CIS457-Project-2-P2P\\fileList.txt";
        HashSet<String> files = getFiles(path);
        for (String file : files) {
            String[] info = file.split(":");
            newFileCommand = info[0] + " " + info[1];
            try {
                sendNewFileCommand(newFileCommand);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private HashSet<String> getFiles(String filePath) {

        HashSet<String> files = new HashSet<>();
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                return new HashSet<>();
            }

            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while (line != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.equals(" ") && !line.equals(System.lineSeparator())) {
                    files.add(line);
                }
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashSet<>();
        }

        return files;
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

    // Needs to send quit to CentralServer somewhere
    public void sendDisconnectCommand(String command) throws IOException {
        disconnectCommand = command + " " + InetAddress.getLocalHost().getHostAddress() + "\r\n";
        out.writeBytes(disconnectCommand);
        System.out.println("Quit message sent to:  " + socket.getInetAddress().getHostAddress());
        out.close();
        in.close();
    }

    // Needs to send to CentralServer somewhere
    public void sendFTPCommand(String command) throws IOException {
        try {
            if (ftpClient == null) {
                ftpClient = new FTPClient(command, 8080);
            } else {
                ftpClient.sendCommand(command);
                ftpClient.run();
                commandline = ftpClient.sendySentence;
                ftpClient.sendySentence = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FTPCommand = command;
    }

    public Set<String> loadPeerInfo() {
        return peerSet;
    }

    public String sendCommandLine() {
        return commandline;
    }

    public void sendSearchCommand(String command) {
        try {
            System.out.println("Searching for: " + searchCommand);
            searchCommand = "search " + command + "\r\n";
            searchCommandSent = true;
            out.writeBytes(searchCommand);
            searchCommand = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkForPeers() {
        try {
            String input = in.readUTF();
            if (input.contains(":")) {
                String[] info = input.split(" ");
                peerSet.addAll(Arrays.asList(info));
            }
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchCommandSent = false;
    }

    public void sendNewFileCommand(String command) throws IOException {
        newFileCommand = "200 " + command + "\r\n";
        out.writeBytes(newFileCommand);
        System.out.println("Files sent to:  " + socket.getInetAddress().getHostAddress());
    }

}
