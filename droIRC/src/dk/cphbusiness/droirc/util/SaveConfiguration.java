package dk.cphbusiness.droirc.util;

import dk.cphbusiness.droirc.entity.Connection;
import dk.cphbusiness.droirc.entity.User;


public class SaveConfiguration {
	private Connection connection;
	private String chatText;
	private User user;
	
	public SaveConfiguration(Connection connection, String chatText, User user) {
		this.connection = connection;
		this.chatText = chatText;
		this.user = user;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
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
	
	
}
