package JavaServerPackage;

import java.io.*;
import java.net.*;
import java.util.*;

import java.time.LocalDate;

public class JavaServerApp {

	private static final int PORT = 5050;
	private static List<ClientHandler> clients = new ArrayList<>();
	private static LocalDate time;
	private static final String CONFIG_DIR = System.getProperty("user.home") + "/JavaChatAppConfig";
	private static final String PORT_FILE = System.getProperty("user.home") + "/port.txt";

	
	
	public static void main(String[] args) {
		
		createConfigDirectory();
		int port = readPortFromFile(PORT_FILE);
		LocalDate time = LocalDate.now();
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("=== Server Started. At: " + time + "===");	
		
		  System.out.println("Current port from file: " + port);
	        System.out.print("Do you want to use this port? (y/n): ");
	        String choice = scanner.nextLine();

	        if (choice.equalsIgnoreCase("n")) {
	            System.out.print("Enter a new port number: ");
	            port = scanner.nextInt();
	            savePortToFile(PORT_FILE, port);
	        }

	        startServer(port);
	
	}
	
	//This is the logic to start the sever
	private static void startServer(int port) {
		LocalDate time = LocalDate.now();
		System.out.println("== Server Started at" + time + "==");
		
		try(ServerSocket serverSocket = new ServerSocket(port)){
			System.out.println("Listening on port: " + port);
			
			while(true) {
				Socket socket = serverSocket.accept();
				System.out.println("New Client Connected: " + socket.getInetAddress());
				//System.out.println("Current clients connected: " + clients.size());
				ClientHandler clientHandler = new ClientHandler(socket, clients);
				System.out.println("Before adding: " + clients.size());
				synchronized (clients) {
	                   clients.add(clientHandler);
                }
				System.out.println("After adding: " + clients.size());
				new Thread(clientHandler).start();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	  private static void createConfigDirectory() {
	        File configDir = new File(System.getProperty("user.home") + "/JavaChatAppConfig");
	        if (!configDir.exists()) {
	            configDir.mkdirs();
	            System.out.println("Created directory: " + configDir.getAbsolutePath());
	        }
	    }
	
	//Read the port from the file
	private static int readPortFromFile(String fileName) {
		  try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
	            return Integer.parseInt(reader.readLine());
	        } catch (IOException | NumberFormatException e) {
	            System.out.println("Port file not found or invalid. Using default port 5050.");
	            return 5050;
	        }
	}
	
	
	
	//Save the port to the file
	private static void savePortToFile(String fileName, int port) {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)) ){
			writer.write(String.valueOf(port));
			System.out.println("Port saved to file:" + " " + port);
		}catch(IOException e) {
			System.out.println("Error saving port file: " + e.getMessage());
		}
	}
}
