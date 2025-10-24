package JavaServerPackage;

import java.io.*;
import java.net.*;
import java.nio.Buffer;
import java.util.*;

public class ClientHandler implements Runnable {

	private Socket socket;
	private List<ClientHandler> clients;
	private PrintWriter out;
	private BufferedReader in;
	private String clientName;
	
	public ClientHandler(Socket socket, List<ClientHandler> clients) {
		this.socket = socket;
		this.clients = clients;
	}
	
	@Override 
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true );
			
			//Ask for the client name
			out.println("Enter your handle name:");
			clientName = in.readLine();
			
		   
			
			System.out.println(clientName + "joined");
			
		
			//Listen messages 
			String message;
			
			while((message = in.readLine()) != null) {
				if(message.equalsIgnoreCase("exit")) {
					break;
				}
				broadcast(clientName + ":" + message);
				
			}
		}catch(IOException e) {
			System.out.println("Error with client:" + e.getMessage());
		}finally {
			try {
				socket.close();
			}catch(IOException e) 
			{
				e.printStackTrace();
			}
		 synchronized (clients) {
                clients.remove(this);
            }
		 System.out.println(clientName + "Disconnected");
		 
		 broadcast(clientName + " " + "left the chat");
		}
		}
		private void broadcast(String message) {
		    synchronized (clients) {
		        for (ClientHandler client : clients) {
		           
		            if (client != this) {
		                client.out.println(message);
		            }
		        }
		    }
		}
		
	}

