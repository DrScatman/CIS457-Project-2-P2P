import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class P2PServer {
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
