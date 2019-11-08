import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CentralServer {
    //This socket waits for client connections.
    private static ServerSocket welcomeSocket;
    //peers is an ArrayList of all clients connected to the CentralServer.
    //private static ArrayList<ClientHandler> handlerList = new ArrayList<ClientHandler>();
    //peerData holds a list of PeerData objects to create a list of all available files across clients.
    public static ArrayList<Peer> userList = new ArrayList<Peer>();
    public static ArrayList<FileData> fileList = new ArrayList<FileData>();
    public static HashMap<Peer, Set<FileData>> map;

    public static void main(String[] args) throws IOException {

        try {
            welcomeSocket = new ServerSocket(8080);
            System.out.println("Server Started");
        } catch (Exception e) {
            System.err.println("Error: Server was not started");
            e.printStackTrace();
        }
        try {
            while(true) {
                Socket connectionSocket = welcomeSocket.accept();

                // Establish I/O streams
                BufferedReader readBuffer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());

                ClientHandler client = new ClientHandler(connectionSocket, readBuffer, out);
                //handlerList.add(client);
                client.start();
            }

        } catch (Exception e) {
            System.err.println("Error: Client could not be connected.");
            e.printStackTrace();
        } finally {
            try {
                //In the event of an error close the welcomeSocket.
                welcomeSocket.close();
                System.out.println("Server socket closed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

/*
class ClientHandler implements Runnable {
    Socket connectionSocket;
    String fromClient;
    String clientName;
    String hostName;
    int port;
    String speed;
    BufferedReader dis;
    DataInputStream in;
    DataOutputStream out;
    boolean loggedIn;

    //Constructor for ClientHandler
    public ClientHandler(Socket connectionSocket, BufferedReader dis, DataOutputStream out) {
        this.connectionSocket = connectionSocket;
        this.dis = dis;
        this.out = out;
        this.loggedIn = true;
    }

    //Run method is overridden to allow multiple clients to use the server.
    @Overrride
    public void run() {
        //connectionString coming from localFtpClient.
        String connectionString;
        String fileList;

        int listSize;

        try {
            //First string received contains the username, hostname, and speed for that specific client.
            in = new DataInputStream(connectionSocket.getInputStream());
            connectionString = is.readUTF();

            //New StringTokenizer containing connectionString.
            String tokens = new StringTokenizer(connectionString);
            this.clientName = tokens.nextToken();
            this.hostName = tokens.nextToken();
            this.speed = tokens.nextToken();
            this.port = Integer.parseInt(tokens.nextToken());

            System.out.println(clientName + " has connection!");

            //reads in if the file has any files available for download.
            fileList = in.readUTF();

            //If no files are available fileList will equal only "404";
            //Might switch all "404" responses to a different number for clarity. Not technically a 404.
            if(!fileList.equals("404")) {
                tokens = new StringTokenizer(fileList);
                //read in next token containing status code.
                String data = tokens.nextToken();

                if(data.startsWith("200")) {
                    //Reads in the number of files available for download.
                    data = tokens.nextToken();
                    listSize = Integer.parseInt(data);

                    for(int i = 0; i < listSize; i++) {
                        //First string of file information.
                        String fileInfo = in.readUTF();
                        tokens = new StringTokenizer(fileInfo);
                        //$ is used to parse here because fileDescription contains spaces.
                        String fileName = tokens.nextToken($);
                        String fileDescription = tokens.nextToken();

                        //new clientdata object with necessary information for the file.
                        PeerData cd = new PeerData(this.clientName, this.hostName, this.port, fileName, fileDescription, this.speed);
                        //add file data to the ArrayList of all available files.
                        CentralServer.peerData.add(cd);
                    }
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            boolean hasNotQuit = true;

            //Parses messages received by the client into a command.
            do {
                //data fromClient
                fromClient = in.readUTF();
                //if output from client equals -1 quit server.
                if(fromClient.equals("-1")) {
                    hasNotQuit = false;
                } else {
                    //Central server file description search.
                    for (int i = 0; i < Centralized_Server.peerData.size(); i++) {
                        if (Centralized_Server.peerData.get(i).fileDescription.contains(fromClient)) {
                            PeerData cd = Centralized_Server.peerData.get(i);
                            String str = cd.speed + " " + cd.hostName + " " + cd.port + " " + cd.fileName + " "
                                    + cd.hostUserName;
                            //sends string containing file information to be downloaded using retrieve.
                            dos.writeUTF(str);
                            System.out.println(cd.fileName);
                        }
                    }
                }
                dos.writeUTF("EOF");
            } while (hasNotQuit);
            //Online status set to offline.
            this.loggedIn = false;
            //remove file from files  ArrayList.
            for (int i = 0; i < Centralized_Server.peerData.size(); i++) {
                if (Centralized_Server.peerData.get(i).hostName == this.hostName) {
                    Centralized_Server.peerData.remove(i);
                }
            }
            //close connectionSocket
            this.connectionSocket.close();
            System.out.println(clientName + "has disconnected!");
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}

*/
