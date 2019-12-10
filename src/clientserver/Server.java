package clientserver;

import java.io.*;
import java.net.*;
import java.util.*;

import core.GameSystem;
import gameobjects.Player;
import iohandling.BoardWriter;

/**
 * This is the class that holds all the information for the server, this holds all the exchanges of the clients, this holds all the information for the 
 * streams connecting the client and server
 * 
 * @author Simon Glew
 *
 */
public class Server {
	private final static int LOGIN_LIMIT = 4;
	
	private Map<Integer, String> IDtoUsername;
	private ArrayList<ClientThread> al;

	private ServerController serverController;
	private TimeThread time;

	private static int uniqueId;
	private int port;
	private boolean keepGoing;

	/**
	 * Constructor for the server, this gets called when the main method of the class is called
	 * 
	 * @param port - The port of the server 
	 */
	public Server(int port) {
		this.port = port;
		al = new ArrayList<ClientThread>();
		IDtoUsername = new HashMap<Integer, String>();
	}

	/**
	 * Method that gets called that starts the server on a new server socket, creates the necessary objects to keep the server running and waits for connections
	 * when a connection is found, it accepts the socket and creates and starts a new client thread 
	 */
	public void start() {
		keepGoing = true;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Waiting for clients on port " + port);
			serverController = new ServerController(this);
			time = new TimeThread();
			time.start();
			while (keepGoing) {
				Socket socket = serverSocket.accept();
				if (!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);
				al.add(t);
				t.start();
			}
			/* If the server is closing */
			try {
				serverSocket.close();
				for (int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
						tc.sInput.close();
						tc.sOutput.close();
						tc.socket.close();
					} catch (IOException e) {

					}
				}
			} catch (Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
			display("Exception on new ServerSocket: " + e + "\n");
		}
	}

	/**
	 * Helper method for displaying something to the console
	 * 
	 * @param msg - Message to be displayed
	 */
	private void display(String msg) {
		System.out.println(msg);
	}

	/**
	 * Method that gets called when wanting to broadcast to all clients 
	 * 
	 * @param packet - the packet object to get broadcasted to clients
	 * @param id - id of client thread that the broadcast is coming from
	 */
	synchronized void broadcast(Packet packet, int id) {
		for (int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			/* If login fail we want to only call it on the id that broke */
			if (packet.getMessage() == "fail login" || packet.getType() == "popupOne") {
				if (ct.id == id) {
					ct.writeToClient(packet);
				}
			}else if(packet.getType() == "popupBarOne"){ 
				if(ct.id != id){
					ct.writeToClient(packet);
				}
			}else {
				if (!ct.writeToClient(packet)) {
					al.remove(i);
					display("Disconnected Client " + ct.id + " removed from list.");
				}
			}
		}
	}
	
	/**
	 * Method that returns the ID that is joined to a specific username
	 * 
	 * @param username - Username that we want to find the ID for
	 * @return ID - ID that is joined the specific Username
	 */
	public int getID(String username){
		for(int i : IDtoUsername.keySet()){
			if(IDtoUsername.get(i).equals(username)){
				return i;
			}
		}
		return 0;
	}

	/**
	 * Method that removes the client from the list, due to client being disconnected
	 * 
	 * @param id - id that needs to be removed
	 */
	synchronized void remove(int id) {
		for (int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			if (ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}
	
	/**
	 * Gets the current server time
	 * 
	 * @return time - current server time
	 */
	public int getServerTime(){
		return time.getTime();
	}

	/**
	 * Main method of the server, this is the method that gets called that makes the server 
	 * 
	 * @param args - Should only be one argument with the server port number
	 */
	public static void main(String[] args) {
		int portNumber = 4518;
		switch (args.length) {
		case 1:
			try {
				portNumber = Integer.parseInt(args[0]);
			} catch (Exception e) {
				return;
			}
		case 0:
			break;
		default:
			return;
		}
		Server server = new Server(portNumber);
		server.start();
	}
	
	/**
	 * Class that is a thread that holds the current time of the server, this increments each second and broadcasts to clients
	 * 
	 * @author Simon Glew
	 */
	class TimeThread extends Thread {
		int count;
		
		/**
		 * Constructor that sets the initial time to 0
		 */
		TimeThread() {
			count = 0;
		}
		
		/**
		 * Method that gets called on startup that broadcasts the current server time each second
		 */
		public void run() {
			while (true) {
				broadcast(new Packet("time", null, null, getTime()), 0);
				count++;
				serverController.tick(count);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

				}
			}
		}
		
		/**
		 * Getter of the current time of the server
		 * 
		 * @return time - current time of the server
		 */
		public int getTime() {
			return count;
		}
	}

	/**
	 * Class that is created whenever a new client is made within the server that creates the streams needed 
	 * 
	 * @author Simon Glew
	 */
	class ClientThread extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;

		int id;
		PlayerCommand cm;

		/**
		 * Constructor that takes in the socket and creates the streams needed for communication between the client and the server
		 * 
		 * @param socket - Socket of the client
		 */
		ClientThread(Socket socket) {
			System.out.println("Client accepted");
			id = ++uniqueId;
			this.socket = socket;
			try {
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
		}

		/**
		 * Method that gets called after client thread is created that listens on the stream, parses the player command and calls the broadcast method with
		 * the right packet being created
		 */
		public void run() {
			boolean keepGoing = true;
			while (keepGoing) {
				try {
					cm = (PlayerCommand) sInput.readObject();
					/*For closing the client thread */
					if (cm.getMessage().equals("close")) {
						this.close();
					}
				} catch (IOException e) {
					display(id + " Exception reading Streams: " + e);
					break;
				} catch (ClassNotFoundException e) {
					break;
				}
				String parsed = serverController.parseInput(cm);
				if (parsed.equals("true")) {
					if (cm.getMessage().contains("login")) {
						/* Check for login limit */
						if (al.size() <= LOGIN_LIMIT) {
							/* Updates map with username */
							IDtoUsername.put(id, cm.getMessage().substring(6));
							/* Broadcast new board */
							broadcast(new Packet("board", BoardWriter.writeBoardToString(serverController.requestBoard()), null, time.getTime()),id);
						} else {
							/* broadcast if you fail to login */
							broadcast(new Packet("string", null, "fail login", 0), id);
							remove(id);
							this.close();
						}
					} else {
						/* Broadcast new board */
						broadcast(new Packet("board", BoardWriter.writeBoardToString(serverController.requestBoard()), null, time.getTime()), id);
					}
				/* broadcast if you fail to login */
				} else if (parsed.equals("fail login")) {
					broadcast(new Packet("string", null, "fail login", 0), id);
					remove(id);
					this.close();
				/* If you cannot move due to something */
				} else if (parsed.equals("false") && cm.getMessage().contains("move")) {
					broadcast(new Packet("board", BoardWriter.writeBoardToString(serverController.requestBoard()), null,  time.getTime()), id);
				/* Broadcast endgame */	
				} else if (parsed.equals("endgame")) {
					System.out.println("a");
					broadcast(new Packet("string", null, "endgame " + IDtoUsername.get(id), time.getTime()), id);
				/* Should not get here */
				} else {
					System.out.println("fail");
				}
			}
			/* remove myself from the arrayList containing the list of the connected Clients, and set you to logged out */
			Player p = serverController.getPlayerByUserName(IDtoUsername.get(id));
			if (p != null) {
				p.setLoggedIn(false);
				IDtoUsername.remove(id);
				p.getTile().setGameObject(null);
			}
			remove(id);
		}

		/**
		 * Method that attempts to close all the connections to the server
		 */
		private void close() {
			try {
				if (sOutput != null)
					sOutput.close();
			} catch (Exception e) {

			}
			try {
				if (sInput != null)
					sInput.close();
			} catch (Exception e) {

			}
			try {
				if (socket != null)
					socket.close();
			} catch (Exception e) {

			}
		}

		/**
		 * Method that attempts to write a packet to the client
		 * 
		 * @param packet - Packet to send
		 * 
		 * @return boolean - If sending was successful
		 */
		private boolean writeToClient(Packet packet) {
			/* If client is still connected send the packet to it */
			if (!socket.isConnected()) {
				close();
				return false;
			}
			try {
				sOutput.writeObject(packet);
			}
			catch (IOException e) {
				display("Error sending message to " + id);
				display(e.toString());
			}
			return true;
		}
	}
}
