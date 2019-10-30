/*
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class P2PServer extends Thread{

    private ServerSocket server;
    private static int id = 0;
    private ArrayList<Peer> peerList;
    private ArrayList<File> files;
    private Socket socket;

    public static void main(String[] args) throws Exception {
        try {
            // sign into the peer-to-peer network,
            // using the username "serverpeer", the password "serverpeerpassword",
            // and create/find a scoped peer-to-peer network named "TestNetwork"
            //System.out.println("Signing into the P2P network...");
            //P2PNetwork.signin("serverpeer", "serverpeerpassword", "TestNetwork");

            // start a server socket for the domain
            // "www.nike.laborpolicy" on port 100
            System.out.println("Creating server socket for " + "www.nike.laborpolicy:100...");
            ServerSocket server = new ServerSocket(8080);

            // wait for a client
            System.out.println("Waiting for client...");
            Socket client = server.accept();
            System.out.println("Client Accepted.");

            // now communicate with this client
            DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            out.writeUTF("Hello client world!");
            String results = in.readUTF();
            System.out.println("Message from client: " + results);
            System.out.println(client.toString());

            // shut everything down!
            client.close();
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void listenSocket(){
        try{
            server = new ServerSocket(4444);
        } catch (IOException e) {
            System.out.println("Could not listen on port 4444");
            System.exit(-1);
        }
        while(true){
            ClientHandler w;
            try{
//server.accept returns a client connection
                w = new ClientWorker(server.accept(), textArea);
                Thread t = new Thread(w);
                t.start();
            } catch (IOException e) {
                System.out.println("Accept failed: 4444");
                System.exit(-1);
            }
        }
    }
    */
/*public void listenSocket() {
        try {
            server = new ServerSocket(4321);
        } catch (IOException e) {
            System.out.println("Could not listen on port 4321");
            System.exit(-1);
        }

        listenSocketSocketserver.acceptSocket();
        try {
            client = server.accept();
        } catch (IOException e) {
            System.out.println("Accept failed: 4321");
            System.exit(-1);
        }

        listenSocketBufferedReaderclientPrintWriter
        try {
            in = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(),
                    true);
        } catch (IOException e) {
            System.out.println("Read failed");
            System.exit(-1);
        }
    }

    listenSocket
    while(true)

    {
        try {
            line = in.readLine();
//Send data back to client
            out.println(line);
        } catch (IOException e) {
            System.out.println("Read failed");
            System.exit(-1);
        }
    }*//*


    //needs to have clients connect to them and share with them the clients connected and the files on them to clients
    public void keywordReceiveAndRespond(){
        BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
        String keyWord = inData.readLine();

        // put some functionality here to choose which response to return: 1- if a file is found, return fileName
        // and hostName; 2- if no file is found, return string "No files were found matching your search criteria."
        DataOutputStream dataToServer = new DataOutputStream(dataSocket.getOutputStream());
        dataToServer.writeBytes("\nKeyword search results:\nHost: " + hostName + "has file: " + fileName);

    }
    public String keywordSearch(String key){
        String keyword = key;


    }
}
*/
