public class P2PClient {

    //needs to get list of clients and their files from the server
    //needs to have FTP client and server implemented, so detects connection and chooses which to be
    //always listening on port 8080 after connecting to server
    //has to send list of clients, and their files, and commands to GUI
    //has to receive commands from the gui for connecting to server and other peers
    //needs to upload file list and descriptions
    private String searchCommand, FTPCommand, connectCommand;
    public void receiveConnectCommand(String command){
        connectCommand = command;
    }

    public void receiveFTPCommand(String command){
        FTPCommand = command;
    }

    public String[][] sendPeerTable(){
        return null;
    }

    public String sendCommandLine(){
        return null;
    }

    public void receiveSearchCommand(String command){
        searchCommand = command;
    }
}
