package dk.cphbusiness.droirc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public class SaveConfiguration {
	private String hostName;
	private String chatText;
	private String channelName;
	private User user;
	private BufferedWriter write;
	private BufferedReader read;
	private Socket socket;
	
	public SaveConfiguration(String hostName, String chatText, String channelName, User user, BufferedWriter write, BufferedReader read, Socket socket) {
		this.hostName = hostName;
		this.chatText = chatText;
		this.channelName = channelName;
		this.user = user;
		this.write = write;
		this.read = read;
		this.socket = socket;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getChatText() {
		return chatText;
	}

	public void setChatText(String chatText) {
		this.chatText = chatText;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public BufferedWriter getWrite() {
		return write;
	}

	public void setWrite(BufferedWriter write) {
		this.write = write;
	}

	public BufferedReader getRead() {
		return read;
	}

	public void setRead(BufferedReader read) {
		this.read = read;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	
	
}
