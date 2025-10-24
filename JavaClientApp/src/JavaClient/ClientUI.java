package JavaClient;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class ClientUI extends JFrame {

	//To do a good UI I need a input a button an area and somwhere to display the chats
	private JTextArea chatArea;
	private JTextField inputField;
	private JButton sendButton, connectButton, changePortButton;
	private JTextField nameField, portField;
	private Socket socket; //The declaration for the connection
	private PrintWriter out;
	private BufferedReader in;
	private Thread listenerThread;
	private Boolean connected = false;
	
	//This create a new file dinamicly to the users pc 
	 private static final String CONFIG_DIR = System.getProperty("user.home") + "/JavaChatAppConfig";
	 private static final String PORT_FILE = System.getProperty("user.home") + "/port.txt";

	public ClientUI() {
		
		setTitle("GamasChats");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500,500);
		setLayout(new BorderLayout());
		
		//The Main panel
		JPanel topPanel = new JPanel(new FlowLayout());
		nameField = new JTextField("Your Name", 10);
		portField = new JTextField("5050",5);
		//Save the  port to the file
		createConfigDirectory();
		int savedPort =  readPortFromFile(PORT_FILE);
		portField.setText(String.valueOf(savedPort));
		connectButton  = new JButton("Connect");
		changePortButton = new JButton("Change Port");
		topPanel.add(new JLabel("Handle:"));
		topPanel.add(nameField);
		topPanel.add(new JLabel("Port:"));
		topPanel.add(portField);
		topPanel.add(connectButton);
		topPanel.add(changePortButton);
		add(topPanel, BorderLayout.NORTH);
		
		//Chat Area
		chatArea = new JTextArea();
		chatArea.setEditable(false); //Disable the posibility to change or edit the messages that have already sent
		add(new JScrollPane(chatArea), BorderLayout.CENTER);
		
		//The second Panel
		
		JPanel bottomPanel = new JPanel(new BorderLayout());
		inputField = new JTextField();
		sendButton = new JButton("send");
		bottomPanel.add(inputField, BorderLayout.CENTER);
		bottomPanel.add(sendButton, BorderLayout.EAST);
		add(bottomPanel, BorderLayout.SOUTH);
		
		setVisible(true);
		connectButton.addActionListener(a -> connectedServer());
		sendButton.addActionListener(a -> sendMessage());
		inputField.addActionListener(a -> sendMessage());
		changePortButton.addActionListener(a -> changePort());
	}
	
	
	
	

	
	//Void to connect with the server
	
	private void connectedServer() {
		if(connected) {
			appendMessage("You are now connected to the server");
            connectButton.setEnabled(false);
			return;
		}
		
		String name = nameField.getText().trim();
		String portText = portField.getText().trim();
		
		if(name.isEmpty() || portText.isEmpty()) {
			appendMessage("You cant leave your name and the port in blank \n");
			return;
		}
		
		int port = Integer.parseInt(portText);
		savePortToFile(PORT_FILE, port);
		
		try {
			socket = new Socket("localhost", port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			connected = true;
			
			String request = in.readLine();
			appendMessage(request + "\n");
			
			out.println(name);
			
			appendMessage("Connected to server on:" + port + "Port \n" );
			
			//Listener Thread
			
			listenerThread = new Thread(() ->{
				try {
					
					String message;
					while((message = in.readLine()) != null) {
						if (!message.startsWith(nameField.getText().trim() + ":")) {
						    appendMessage(message + "\n");
						}
						//appendMessage(message + "\n");
					}
				}catch(IOException e) {
					appendMessage("connection lost");
				}
				
			});
			//Start the thread
			listenerThread.start();
		}catch(IOException e ) {
			appendMessage("Error Connecting to the server, please try again" + e.getMessage() + "\n");
		}
	}
	
	private void appendMessage(String msg) {
		chatArea.append(msg);
		chatArea.setCaretPosition(chatArea.getDocument().getLength());
	}
	
	public void sendMessage() {
		if(!connected) {
			appendMessage("You are not connected to the server");
			return;
		}
		
		String message = inputField.getText().trim();
		if(!message.isEmpty()) {
		   out.println(message);
		   inputField.setText("");
		   appendMessage("(You): " + message + "\n");
		}
	}
	
	private void changePort() {
	    String portText = portField.getText().trim();

	    try {
	        int newPort = Integer.parseInt(portText);
	        if (newPort < 0 || newPort > 999999) {
	            appendMessage("Port must be between 1 and 999999.\n");
	            return;
	        }

	        savePortToFile(PORT_FILE, newPort);
	        appendMessage("New port " + newPort + " saved successfully.\n");
	        appendMessage("Please restart the server before connecting.\n");

	        if (connected) {
	            appendMessage("You are already in the server");

	            try {
	                socket.close();
	            } catch (IOException e) {
	                appendMessage("Error closing socket: " + e.getMessage() + "\n");
	            }
	            connected = false;
	            
	        }

	    } catch (NumberFormatException e) {
	        appendMessage("Invalid port number.\n");
	    }
	}
	
	//Voids to do the directory and file connection to the ports
	
	private void createConfigDirectory() {
	    File configDir = new File(CONFIG_DIR);
	    if (!configDir.exists()) {
	        configDir.mkdirs();
	        System.out.println("Created directory: " + configDir.getAbsolutePath());
	    }
	}
	
	//Reading the port from the file
	
	private int readPortFromFile(String fileName) {
		try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
			return Integer.parseInt(reader.readLine());
			
		}catch(IOException e) {
			System.out.println("No port file found. Using default 5050.\n");
	         return 5050;
		}
	}
	
	//Save the port to the file
	private void savePortToFile(String fileName, int port) {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
			writer.write(String.valueOf(port));
			System.out.println("Port saved to file: " + port + "\n");
		}catch(IOException e) {
			System.out.println("Error saving port file: " + e.getMessage() + "\n");
		}
	}
	public static void main(String[] args) {
		SwingUtilities.invokeLater(ClientUI::new);
	}
}
