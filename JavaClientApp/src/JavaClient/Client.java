package JavaClient;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
	private static final String SERVER_ADDRESS = "localhost";
	private static final int PORT = 5050;
	
	public static void main(String[] args) {
		try(Socket socket = new Socket(SERVER_ADDRESS, PORT)){
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			Scanner scanner = new Scanner(System.in);
			
			//This read the server message
			System.out.println(in.readLine());
			String name = scanner.nextLine();
			out.println(name);
			
			//This is a basic Thread to read the messages from the server
			
			Thread listener = new Thread(() -> {
				try {
					String msgServer;
					
					while((msgServer = in.readLine()) != null) {
						System.out.println(msgServer);
						
					}
				}catch(IOException  e) {
					System.out.println("Connection closed.");
				}
			});
			listener.start();
			
			System.out.println("Hey lets chat! Type 'Exit' to quit");
			
			//If the user type exit the program is going to break	
			while(true) {
				String msg = scanner.nextLine();
				out.println(msg);
				if(msg.equalsIgnoreCase("exit")) {
					break;
				}
				
			}
			//Close the connection with the server
			socket.close();
			System.out.println("Disconected from server");
		}catch(IOException e ){
			System.out.println("Error connecting to server: " + e.getMessage());
		}
	}
}
