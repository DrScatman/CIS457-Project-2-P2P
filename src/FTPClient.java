import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

class FTPClient{
    
    private Socket controlSocket;
    private int port;
    private String sentence;
    public String sendySentence;
    DataOutputStream outToServer;
    DataInputStream inFromServer;

    public FTPClient(String serverIp, int port) throws Exception {
        /*String sentence;

        System.out.println("IP address, and port number to connect");

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        sentence = inFromUser.readLine();

        StringTokenizer tokens = new StringTokenizer(sentence);

        String serverName = tokens.nextToken();
        port1 = Integer.parseInt(tokens.nextToken());
        System.out.println(port1);
        System.out.println("You are connected to " + serverName);*/
        this.port = port;
        controlSocket = new Socket(serverIp, port);
        outToServer = new DataOutputStream(controlSocket.getOutputStream());

        inFromServer = new DataInputStream(new BufferedInputStream(controlSocket.getInputStream()));
        // Started by caller this.start();
    }

    public void sendCommand(String sentence) {
        this.sentence = sentence;
    }


    public void run() {
        try {
            while (sentence != null && !sentence.isEmpty()) {
                String modifiedSentence;


                //String sentence = inFromUser.readLine();

                if (sentence.equals("list:")) {
                    
                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + '\n');

                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();
                    BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
                    while (inData.read() != -1) {
                        modifiedSentence = inData.readLine();
                        //System.out.println(modifiedSentence);
                        sendySentence += modifiedSentence;
                    }

                    inData.close();
                    welcomeData.close();
                    dataSocket.close();
                    //System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");
                    sendySentence += "\nWhat would you like to do next: \n retr: file.txt || quit:\n";
                } else if (sentence.startsWith("retr: ")) {
                    StringTokenizer tok = new StringTokenizer(sentence);
                    tok.nextToken();
                    String fileName = tok.nextToken();

                    port += 2;

                    outToServer.writeBytes(port + " " + sentence + '\n');
                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();
                    BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

                    String read = inData.readLine();
                    if (read.equals("550")) {
                        //System.out.println("550 Cannot find file");
                        sendySentence += "550 Cannot find file\n";
                        //sendySentence +=null;
                    }
                    if (read.equals("200 OK")) {
                        //System.out.println("200 OK");
                        sendySentence+="200 OK\n";
                        //sendySentence=null;
                        File file = new File(fileName);
                        OutputStream out = new FileOutputStream(file);
                        out.write(inData.read());
                        out.close();
                    }
                    inData.close();
                    welcomeData.close();
                    dataSocket.close();

                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");
                    sendySentence += "\nWhat would you like to do next: \n retr: file.txt || quit:\n";

                } else if (sentence.startsWith("stor: ")) {
                    StringTokenizer tok = new StringTokenizer(sentence);
                    tok.nextToken();
                    String fileName = tok.nextToken();
                    port += 2;

                    outToServer.writeBytes(port + " " + sentence + '\n');
                    ServerSocket socket = new ServerSocket(port);
                    Socket dataSocket = socket.accept();

                    DataOutputStream dataToServer = new DataOutputStream(dataSocket.getOutputStream());
                    BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

                    //if file does not exist on server
                    String read = inData.readLine();
                    if (read.equals("200 OK")) {
                        //PATH should be directory of client
                        File folder = new File("C:\\Users\\bunny\\Desktop\\folder");
                        String[] files = folder.list();
                        //finding our file in directory and sending it
                        boolean found = false;
                        for (String file : files) {
                            if (file.equals(fileName)) {
                                found = true;
                                System.out.println("200 OK");
                                dataToServer.writeBytes("200 OK");
                                dataToServer.writeBytes("\n");
                                FileInputStream fis = new FileInputStream("C:\\Users\\bunny\\Desktop\\folder\\" + fileName);
                                //add headers and stuff and codes
                                sendBytes(fis, dataToServer);
                                fis.close();
                            }
                        }
                        if (!found) {
                            System.out.println("Could not find file in client directory");
                            outToServer.writeBytes("550");
                            outToServer.writeBytes("\n");
                        }
                    }
                    //if file exists on server
                    if (read.equals("550")) {
                        System.out.println("550 File already exists on server");
                    }
                    socket.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

                } else if (sentence.startsWith("quit:")) {

                    port += 2;
                    outToServer.writeBytes(port + " " + sentence + '\n');
                    controlSocket.close();
                    //System.out.println("Connection closed");
                    sendySentence += "Connection closed\n";
                    //sendySentence = null;
                    return;
                } else {
                    //System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");
                    sendySentence += "\nWhat would you like to do next: \n retr: file.txt || quit:\n";
                    //sendySentence=null;
                }
                //sendySentence=null;
                sentence = null;
            }
        } catch (Exception e){
            sentence = null;
            e.printStackTrace();
        }
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes = 0;

        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }
}