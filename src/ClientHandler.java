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
        while (socket.isConnected() && !socket.isClosed()) {
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
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Client: " + socket.getRemoteSocketAddress() + " disconnected");
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
                StringBuilder desc = new StringBuilder();
                while(tokens.hasMoreTokens()) {
                    desc.append( tokens.nextToken()).append(" ");
                }

//                String fileDescription = tokens.nextToken();
                FileData fileData;

                fileData = new FileData(fileName, desc.toString());
                System.out.println("User: " + this.peer.getHostUserName() + " Added -> " + fileData.toString());
                CentralServer.map.get(this.peer).add(fileData);
                System.out.println(CentralServer.map.size() + " Peers");
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
        Set<Peer> set = new HashSet<>(CentralServer.map.keySet());

        for (Peer peer : set) {
            if (peer.getIpAddress().contains(searchKey)) {
                System.out.println("Removing: " + peer.getIpAddress());
                CentralServer.map.remove(peer);
            }
        }

        System.out.println("Peers connected: " + CentralServer.map.size());

        out.close();
        readBuffer.close();
        socket.close();
    }

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
            String searchKey = "";
            if (tokens.hasMoreTokens()) {
                searchKey = tokens.nextToken();
            }

//            if (search.equals("search")) {
            StringBuilder result = new StringBuilder();
            for (Map.Entry<Peer, Set<FileData>> entry : CentralServer.map.entrySet()) {
                for (FileData file : entry.getValue()) {

                    if (file.getFileDescription().toLowerCase().contains(searchKey.toLowerCase())) {
                        Peer peer = entry.getKey();
                        result.append(peer.getSpeed()).append(":").append(peer.getIpAddress()).append(":").append(file.getFileName()).append(" ");
                        //out.writeUTF(peer.getSpeed() + ":" + peer.getIpAddress() + ":" + file.getFileName() + " ");
                    }
                }
            }
            out.writeUTF(result.toString());
            //out.writeByte(peersWithMatchingFiles.size());
//            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //might need bigger buffer depending on file
    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes = 0;

        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }
}

