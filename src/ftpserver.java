import java.io.*; 
import java.net.*;
import java.util.*;
class FTPServer {

	public static void main(String argv[]) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(12000);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			ClientHandler handler = new ClientHandler(connectionSocket);
			handler.start();
		}
	}
}