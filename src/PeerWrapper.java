import java.util.ArrayList;

public class PeerWrapper {

    //peers is an ArrayList of all clients connected to the CentralServer.
    private static ArrayList<ClientHandler> handlerList = new ArrayList<ClientHandler>();
    //peerData holds a list of PeerData objects to create a list of all available files across clients.
    private static ArrayList<Peer> userList = new ArrayList<Peer>();
    private static ArrayList<FileData> fileList = new ArrayList<FileData>();

    public static ArrayList<ClientHandler> getHandlerList() {
        return handlerList;
    }

    public static ArrayList<Peer> getUserList() {
        return userList;
    }

    public static ArrayList<FileData> getFileList() {
        return fileList;
    }

    public static void addHandler(ClientHandler peer) {
        handlerList.add(peer);
    }

    public static void addUser(Peer user) {
        userList.add(user);
    }

    public static void addFileData(FileData fileData) {
        fileList.add(fileData);
    }
}
