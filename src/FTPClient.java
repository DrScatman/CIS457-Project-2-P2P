import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

class FTPClient {

    public static void main(String argv[]) throws Exception {
        String sentence;
        String modifiedSentence;
        int port1;

        System.out.println("Enter connect, IP address, and port number to connect");

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);


        if (sentence.startsWith("connect")) {
            String serverName = tokens.nextToken(); // pass the connect command
            serverName = tokens.nextToken();
            port1 = Integer.parseInt(tokens.nextToken());
            System.out.println(port1);
            System.out.println("You are connected to " + serverName);

            Socket ControlSocket = new Socket(serverName, port1);

            while (true) {

                DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());

                DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));

                sentence = inFromUser.readLine();

                int port = port1;

                if (sentence.equals("list:")) {


                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + '\n');

                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();
                    BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
                    while (inData.read() != -1) {
                        modifiedSentence = inData.readLine();
                        System.out.println(modifiedSentence);
                    }

                    inData.close();
                    welcomeData.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

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
                    if (read.equals("550")){
                        System.out.println("550 Cannot find file");
                    }
                    if (read.equals("200 OK")){
                        System.out.println("200 OK");
                        File file = new File(fileName);
                        OutputStream out = new FileOutputStream(file);
                        out.write(inData.read());
                        out.close();
                    }
                    inData.close();
                    welcomeData.close();
                    dataSocket.close();

                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

                } else if (sentence.startsWith("stor: ")){
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
                    if (read.equals("550")){
                        System.out.println("550 File already exists on server");
                    }
                    socket.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

                } else if (sentence.startsWith("quit:")){

                    port += 2;
                    outToServer.writeBytes(port + " " + sentence + '\n');
                    ControlSocket.close();
                    System.out.println("Connection closed");
                    return;
                } else {
                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");
                }
            }
        } else {
            System.out.println("Enter the correct arguments");
        }


    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception{
        byte[] buffer = new byte[1024];
        int bytes = 0;

        while ((bytes = fis.read(buffer)) != -1){
            os.write(buffer, 0, bytes);
        }
    }
}