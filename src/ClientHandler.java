import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


public class ClientHandler extends Thread {
    Socket socket;
    String fromClient;
    String clientName;
    String hostName;
    String speed;
    BufferedReader readBuffer;
    DataOutputStream out;
    private Peer peer;

    public ClientHandler(Socket connection) throws Exception {
        super();
        this.socket = connection;
        System.out.println("Client connected " + socket.getInetAddress());
    }

    public ClientHandler(Socket connection, BufferedReader readBuffer, DataOutputStream out) throws Exception {
        super();
        this.socket = connection;
        this.readBuffer = readBuffer;
        this.out = out;

        System.out.println("Client connected " + socket.getInetAddress());
    }

    public void startFTP() {
        this.start();
    }

    @Override
    public void run() {
        try {
            processPeerData();
            while (socket.isConnected()) {
                processPeerFiles();
                //processRequest();
            }
        } catch (Exception e) {
            System.out.println("Client Disconnected");
        }

    }

    private void processPeerData() {
        try {
            // First string received contains the username, hostname, and speed for the client
            //in = new DataInputStream(socket.getInputStream());
            String fromClient = readBuffer.readLine();

            // Delimited with spaces
            StringTokenizer tokens = new StringTokenizer(fromClient);
            clientName = tokens.nextToken();
            hostName = tokens.nextToken();
            speed = tokens.nextToken();

            out.writeUTF("Successfully Connected To: " + socket.getInetAddress().getHostAddress());
            Peer peer = new Peer(clientName, hostName, speed);
            this.peer = peer;
            CentralServer.userList.add(peer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processPeerFiles() {
        try {
            String files = readBuffer.readLine();

            // 404 if no files exist ?
            if (!files.equals("404")) {
                StringTokenizer tokens = new StringTokenizer(files);
                String data = tokens.nextToken();

                if (data.startsWith("200")) {
                    //Reads in the number of files available for download.
                    data = tokens.nextToken();
                    int numFiles = Integer.parseInt(data);

                    HashSet<FileData> clientsFiles = new HashSet<>();
                    for (int i = 0; i < numFiles; i++) {
                        // Second line contains file info?
                        String fileInfo = readBuffer.readLine();
                        tokens = new StringTokenizer(fileInfo);
                        String fileName = tokens.nextToken(" ");
                        String fileDescription = tokens.nextToken();

                        // FileData encapsulates information for the file
                        FileData fileData = new FileData(fileName, fileDescription);
                        clientsFiles.add(fileData);
                        CentralServer.fileList.add(fileData);
                    }
                    CentralServer.map.put(peer, clientsFiles);
                }
            }

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
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }

    }

    private void processRequest() throws Exception {
        DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String fromClient = inFromClient.readLine();

        //may need to change where it get the port number
        StringTokenizer tokens = new StringTokenizer(fromClient);
        String frstln = tokens.nextToken();
        int port = Integer.parseInt(frstln);
        String clientCommand = tokens.nextToken();
        System.out.println(clientCommand + socket.getInetAddress());
        if (clientCommand.equals("list:")) {

            Socket dataSocket = new Socket(socket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

            //For when we need to use on a machine, set PATH to directory of server
            File folder = new File("C:\\Users\\bunny\\IdeaProjects\\CIS457Proj1");
            String[] files = folder.list();

            for (String file : files) {
                dataOutToClient.writeBytes(file);
                dataOutToClient.writeBytes(" ");
            }

            dataOutToClient.writeBytes("\n");

            dataOutToClient.close();
            dataSocket.close();
            System.out.println("Data Socket closed");
        }


        if (clientCommand.startsWith("retr:")) {
            Socket dataSocket = new Socket(socket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

            String fileName = tokens.nextToken();//starting after the space
            File folder = new File("C:\\Users\\bunny\\Desktop\\folder");
            String[] files = folder.list();

            //finding our file in directory and sending it
            boolean found = false;
            for (String file : files) {
                if (file.equals(fileName)) {
                    found = true;
                    dataOutToClient.writeBytes("200 OK");
                    dataOutToClient.writeBytes("\n");
                    FileInputStream fis = new FileInputStream("C:\\Users\\bunny\\Desktop\\folder\\" + fileName);
                    sendBytes(fis, dataOutToClient);
                    fis.close();
                }
            }
            //if file is not found send 550
            if (!found) {
                dataOutToClient.writeBytes("550");
                dataOutToClient.writeBytes("\n");
            }
            dataOutToClient.close();
            dataSocket.close();
            System.out.println("Data Socket closed");
        }

        if (clientCommand.startsWith("stor:")) {
            Socket dataSocket = new Socket(socket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

            //should turn into a file need to add codes and stuff
            String fileName = tokens.nextToken();
            File file = new File(fileName);
            BufferedReader dataIn = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

            File folder = new File("C:\\Users\\bunny\\IdeaProjects\\CIS457Proj1");
            String[] files = folder.list();
            boolean found = false;
            //check if file is found on server
            for (String find : files) {
                if (find.equals(fileName)) {
                    found = true;
                    dataOutToClient.writeBytes("550");
                    dataOutToClient.writeBytes("\n");
                }
            }
            //if file is not found on server send ok
            if (!found) {
                dataOutToClient.writeBytes("200 OK");
                dataOutToClient.writeBytes("\n");
            }
            String check = dataIn.readLine();

            if (check.equals("200 OK") && !found) {
                OutputStream byteWriter = new FileOutputStream(file);
                byteWriter.write(dataIn.read());
                byteWriter.close();
            }


            dataOutToClient.close();
            dataSocket.close();
            System.out.println("Data Socket closed");
        }


        if (clientCommand.equals("quit:")) {
            socket.close();
            System.out.println("Client has disconnected");
            return;
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

