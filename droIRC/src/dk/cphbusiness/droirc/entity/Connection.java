package dk.cphbusiness.droirc.entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class Connection {

	private User user;
	private String hostName;
	private Socket socket;
	private String line;
	private ArrayList<String> channels;
	private BufferedWriter write;
	private BufferedReader read;
	private static boolean connected = false;
	private int port;

	public Connection(String hostName, int port, User user) {
		this.hostName = hostName;
		this.port = port;
		this.user = user;
		channels = new ArrayList<String>();
	}

	public void connect() {
		if (!connected) {
			// Initializing connection and streams
			try {
				connected = true;
				socket = new Socket(hostName, port);
				write = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				read = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// Log on to the server.
				write.write("NICK " + user.getNickname() + "\r\n");
				write.write("USER " + user.getUserid() + " 8 * : droIRC 1.0\r\n");
				write.flush();
			}
			catch (UnknownHostException uhoste) {
				uhoste.printStackTrace();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		else
			System.err.println("Already connected");
	}

	public void joinChannel(String channelName) {
		try {
			write.write("JOIN " + channelName + "\r\n");
			write.flush( );
			channels.add(channelName);
		}
		catch (IOException e) {
			System.out.println("Error joining channel");
			e.printStackTrace();
		}
	}

	public String getLine() {
		return line;
	}

	public String getHostName() {
		return hostName;
	}

	public User getUser() {
		return user;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public BufferedReader getReader() {
		return read;
	}

	public BufferedWriter getWriter() {
		return write;
	}

	public ArrayList<String> getChannels() {
		return channels;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public void setChannels(ArrayList<String> channels) {
		this.channels = channels;
	}

	public void setWrite(BufferedWriter write) {
		this.write = write;
	}

	public void setRead(BufferedReader read) {
		this.read = read;
	}

	public static void setConnected(boolean connected) {
		Connection.connected = connected;
	}

	public Socket getSocket() {
		return socket;
	}

	public void disconnect() {
		try {
			write.write("DISCONNECT");
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		connected = false;
		channels = null;
		hostName = null;
		write = null;
		read = null;
	}
}


//public void message(String msg, String channelName) {
//try {			
//	if (msg.equals("")) return; // If message is an empty string don't write to server. Blocks onClick from sending empty lines when clicking EditText field
//	if (msg.length() > 7 && msg.substring(0, 5).equalsIgnoreCase("/join")) { // check if user writes /join
//		joinChannel(msg.substring(6));
//	}
//	else {
//		write.write("PRIVMSG " + channelName + " :" + msg + "\r\n");
//		write.flush();
//	} 
//	//scrollToBottom();
//}
//catch (IOException ioe) {
//	ioe.printStackTrace();
//}
//}