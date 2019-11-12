import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

public class CentralServer {
    //This socket waits for client connections.
    private static ServerSocket welcomeSocket;
    public static HashMap<Peer, Set<FileData>> map = new HashMap<>();

    public static void main(String[] args) throws IOException {
    //server
        try {
            welcomeSocket = new ServerSocket(8081);
            System.out.println("Server Started");

        } catch (Exception e) {
            System.err.println("Error: Server was not started");
            e.printStackTrace();
        }
        //client
        try {
            while(true) {
                Socket connectionSocket = welcomeSocket.accept();
                ClientHandler clientHandler = new ClientHandler(connectionSocket);
                clientHandler.start();
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