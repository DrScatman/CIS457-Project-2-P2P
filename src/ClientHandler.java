import java.io.*;
import java.net.Socket;
import java.util.*;


public class ClientHandler extends Thread {
    Socket socket;
    String fromClient;
    String clientName;
    String hostName;
    String speed;
    String data;
    StringTokenizer tokens;
    BufferedReader readBuffer;
    DataOutputStream out;
    private Peer peer;
    int i = 1;

    public ClientHandler(Socket connection) throws Exception {
        super();
        this.socket = connection;
         readBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         out = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client connected " + socket.getInetAddress());
    }

    public ClientHandler(Socket connection, BufferedReader readBuffer, DataOutputStream out) throws Exception {
        super();
        this.socket = connection;
        this.readBuffer = readBuffer;
        this.out = out;

        System.out.println("Client connected " + socket.getInetAddress() + " socket channel: " + socket.getRemoteSocketAddress());
    }

    /**
     * add disconnect
     **/
    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                fromClient = readBuffer.readLine();

                if (fromClient != null) {
                    // Delimited with spaces
                    if (!fromClient.equals("")) {
                        tokens = new StringTokenizer(fromClient);
                        data = tokens.nextToken();

                        if (data.equals("newPeer")) {
                            processPeerData();
                        }

                        if (data.equals("200")) {
                            processPeerFiles();
                        }

                        if (data.equals("search")) {
                            processSearchRequest();
                        }
                        if (data.equals("quit:")) {
                            disconnectPeer();
                        }
                    } else {
                        tokens = null;
                        data = null;
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void processPeerData() {
        try {
            // First string received contains the username, hostname, and speed for the client
            //tokens.nextToken();

            clientName = tokens.nextToken();
            hostName = tokens.nextToken();
            speed = tokens.nextToken();

            //out.writeUTF("Successfully connected to host: " + socket.getInetAddress().getHostAddress());
            String[] ip = socket.getRemoteSocketAddress().toString().split(":");
            Peer peer = new Peer(clientName, hostName, speed, ip[0]);
            this.peer = peer;
            HashSet<FileData> fileData = new HashSet<FileData>();
            CentralServer.map.put(this.peer, fileData);
            System.out.println("User: " + peer.getHostUserName() + " @ " + peer.getIpAddress() + " has joined. Total users: " + CentralServer.map.size());

        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("Process Peer Data Error");
        }
    }

    /**
     * if peer for file exists then add it to that peer's list of files, else create a new peer
     **/
    private void processPeerFiles() {
        try {
            if (data.equals("200")) {
                //Reads in the number of files available for download.
                String fileName = tokens.nextToken();
                String fileDescription = tokens.nextToken();
                FileData fileData;

                fileData = new FileData(fileName, fileDescription);
                System.out.println("User: " + this.peer.getHostUserName() + " Added -> " + fileData.toString());
                CentralServer.map.get(this.peer).add(fileData);
                System.out.println(CentralServer.map.get(this.peer).size());
            }
        } catch (Exception e) {
            System.out.println("Process Peer File Error");
        }
    }

    /**
     * Searches for the files the client is requesting
     **/
    private HashSet<? extends Object> processRequest() {
        try {
//            System.out.println(new DataInputStream(socket.getInputStream()).readUTF());
//            String keywords = new DataInputStream(socket.getInputStream()).readUTF();

//            StringTokenizer tokens = new StringTokenizer(keywords);
//            String search = tokens.nextToken();

//            if (data.equals("search")) {
            System.out.println("Process");
            String searchKey = tokens.nextToken();
            HashSet<Peer> peersWithMatchingFiles = new HashSet<>();

            for (Map.Entry<Peer, Set<FileData>> entry : CentralServer.map.entrySet()) {
                for (FileData file : entry.getValue()) {

                    if (file.getFileDescription().contains(searchKey)) {
                        peersWithMatchingFiles.add(entry.getKey());
                    }
                }
            }
            return peersWithMatchingFiles;

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return new HashSet<>();
    }

    public void disconnectPeer() throws IOException {
        String searchKey = tokens.nextToken();
        System.out.println(searchKey);
        for (Peer peer : CentralServer.map.keySet()) {
            if (peer.getIpAddress().equals(searchKey)) {
                CentralServer.map.remove(peer);
            }
        }
        out.close();
        readBuffer.close();
    }
    //int numFiles = Integer.parseInt(data);

    // HashSet<FileData> clientsFiles = new HashSet<>();
//                    for (int i = 0; i < numFiles; i++) {
//                        // Second line contains file info?
//                        String fileInfo = readBuffer.readLine();
//                        tokens = new StringTokenizer(fileInfo);
//                        String fileName = tokens.nextToken(" ");
//                        String fileDescription = tokens.nextToken();
//
//                        // FileData encapsulates information for the+ file
    //FileData fileData = new FileData(fileName, fileDescription);
//
//                        CentralServer.fileList.add(fileData);
//                    }
    //CentralServer.map.put(peer, clientsFiles);
           /* boolean hasNotQuit = true;

            //Parses messages received by the client into a command.
            do {
                //data fromClient
                fromClient = readBuffer.readLine();
                //if output from client equals -1 quit server.
                if (fromClient.equals("-1")) {
                    hasNotQuit = false;
                } else {
                    //Central server file description search.
                    for (FileData data : CentralServer.fileList) {
                        if (data.getFileDescription().contains(fromClient)) {
                            String str = speed + " " + clientName + " " + 8080 + " " + data.getFileName() + " "
                                    + hostName;
                            //sends string containing file information to be downloaded using load.
                            out.writeUTF(str);
                            System.out.println(data.getFileName());
                        }
                    }
                }
                out.writeUTF("EOF");
            } while (hasNotQuit);

            //remove file from files  ArrayList.
            for (int i = 0; i < Centralized_Server.peerData.size(); i++) {
                if (Centralized_Server.peerData.get(i).hostName == this.hostName) {
                    Centralized_Server.peerData.remove(i);
                }
            }
            //close connectionSocket
            this.connectionSocket.close();
            System.out.println(clientName + "has disconnected!");*/


    /**
     * Searches for the files the client is requesting
     **/
    private void processSearchRequest() {
        try {
            System.out.println("Process");
//            System.out.println(new DataInputStream(socket.getInputStream()).readUTF());
//            String keywords = new DataInputStream(socket.getInputStream()).readUTF();

//            StringTokenizer tokens = new StringTokenizer(keywords);
//            String search = tokens.nextToken();
            String searchKey = tokens.nextToken();

//            if (search.equals("search")) {

            for (Map.Entry<Peer, Set<FileData>> entry : CentralServer.map.entrySet()) {
                for (FileData file : entry.getValue()) {
                    if (file.getFileDescription().contains(searchKey)) {
                        Peer peer = entry.getKey();
                        out.writeUTF(peer.getSpeed() + ":" + peer.getIpAddress() + ":" + file.getFileName() + " ");
                    }
                }
            }
            //out.writeByte(peersWithMatchingFiles.size());
//            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
//        DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
//        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//        String fromClient = inFromClient.readLine();
//
//        //may need to change where it get the port number
//        StringTokenizer tokens = new StringTokenizer(fromClient);
//        String frstln = tokens.nextToken();
//        int port = Integer.parseInt(frstln);
//        String clientCommand = tokens.nextToken();
//        System.out.println(clientCommand + socket.getInetAddress());
//        if (clientCommand.equals("list:")) {
//
//            Socket dataSocket = new Socket(socket.getInetAddress(), port);
//            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
//
//            //For when we need to use on a machine, set PATH to directory of server
//            File folder = new File("C:\\Users\\hongsyp\\Desktop\\Files2Share");
//            String[] files = folder.list();
//
//            for (String file : files) {
//                dataOutToClient.writeBytes(file);
//                dataOutToClient.writeBytes(" ");
//            }
//
//            dataOutToClient.writeBytes("\n");
//
//            dataOutToClient.close();
//            dataSocket.close();
//            System.out.println("Data Socket closed");
//        }
//
//
//        if (clientCommand.startsWith("retr:")) {
//            Socket dataSocket = new Socket(socket.getInetAddress(), port);
//            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
//
//            String fileName = tokens.nextToken();//starting after the space
//            File folder = new File("C:\\Users\\hongsyp\\Desktop\\Files2Share");
//            String[] files = folder.list();
//
//            //finding our file in directory and sending it
//            boolean found = false;
//            for (String file : files) {
//                if (file.equals(fileName)) {
//                    found = true;
//                    dataOutToClient.writeBytes("200 OK");
//                    dataOutToClient.writeBytes("\n");
//                    FileInputStream fis = new FileInputStream("C:\\Users\\hongsyp\\Desktop\\Files2Share" + fileName);
//                    sendBytes(fis, dataOutToClient);
//                    fis.close();
//                }
//            }
//            //if file is not found send 550
//            if (!found) {
//                dataOutToClient.writeBytes("550");
//                dataOutToClient.writeBytes("\n");
//            }
//            dataOutToClient.close();
//            dataSocket.close();
//            System.out.println("Data Socket closed");
//        }
//
//        if (clientCommand.startsWith("stor:")) {
//            Socket dataSocket = new Socket(socket.getInetAddress(), port);
//            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
//
//            //should turn into a file need to add codes and stuff
//            String fileName = tokens.nextToken();
//            File file = new File(fileName);
//            BufferedReader dataIn = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
//
//            File folder = new File("C:\\Users\\hongsyp\\Desktop\\Files2Share");
//            String[] files = folder.list();
//            boolean found = false;
//            //check if file is found on server
//            for (String find : files) {
//                if (find.equals(fileName)) {
//                    found = true;
//                    dataOutToClient.writeBytes("550");
//                    dataOutToClient.writeBytes("\n");
//                }
//            }
//            //if file is not found on server send ok
//            if (!found) {
//                dataOutToClient.writeBytes("200 OK");
//                dataOutToClient.writeBytes("\n");
//            }
//            String check = dataIn.readLine();
//
//            if (check.equals("200 OK") && !found) {
//                OutputStream byteWriter = new FileOutputStream(file);
//                byteWriter.write(dataIn.read());
//                byteWriter.close();
//            }
//
//
//            dataOutToClient.close();
//            dataSocket.close();
//            System.out.println("Data Socket closed");
//        }
//
//
//        if (clientCommand.equals("quit:")) {
//            socket.close();
//            System.out.println("Client has disconnected");
//            return;
//        }


    //might need bigger buffer depending on file
    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes = 0;

        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }
}

