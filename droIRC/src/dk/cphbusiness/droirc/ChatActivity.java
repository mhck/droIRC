package dk.cphbusiness.droirc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ChatActivity extends Activity {

	private boolean connected = false;
	private Socket socket;
	private String server = "asimov.freenode.net";
	private int port = 6667;
	private String nickname = "Drobot";
	private String user = "Drobot";
	private String line;
	private String channelName = "#droirc";
	private BufferedWriter write;
	private BufferedReader read;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		new ServerConnecter().execute("");
	}

	public boolean connect(String hostName, int port) {
		try {
			// Initializing connection and streams
			socket = new Socket(hostName, port);
			write = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			read = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Log on to the server.
			write.write("NICK " + nickname + "\r\n");
			write.write("USER " + user + " 8 * : Droirc bot 0.1\r\n");
			write.flush();

			// Waiting for server to respond with 004 (logged in)
			while ((line = read.readLine()) != null) {
				System.out.println(line);
				if (line.indexOf("004") >= 0) {
					return true;
				}
				else if (line.indexOf("433") >= 0) {
					System.out.println("Nickname is already in use.");
					return false;
				}
			}
		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return false;
	}

	public void joinChannel(String channelName) {
		try {
			write.write("JOIN " + channelName + "\r\n");
			write.flush( );
		}
		catch (IOException e) {
			System.out.println("Error joining channel");
			e.printStackTrace();
		}
	}

	public void message(View view) {
		try {			
			EditText editText = (EditText) findViewById(R.id.editText1);
			String message = editText.getText().toString();
			write.write("PRIVMSG " + channelName + " :" + message + "\r\n");
			TextView chatArea = (TextView) findViewById(R.id.textView1);
			chatArea.append("<" + nickname + "> " + message + "\n");
			editText.setText("");
			write.flush();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	private class ServerConnecter extends AsyncTask<String, String, Void> {		
		@Override
		protected Void doInBackground(String... params) {
			connect(server, port);
			joinChannel(channelName);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			//super.onPostExecute(result);
			new ServerListener().execute("");
		}
	}

	public String processLine(String line) {
		int indexNickStart = 0, indexNickEnd = 0, indexMsgStart = 0;
		boolean nickFound = false;
		if (line.indexOf(server) >= 0) // if server msg
			return line;
		if (line.indexOf("~" + nickname) >= 0) // notice from server e.g. :drobot!~drobot@188-179-73-182-static.dk.customer.tdc.net JOIN #droirc
			return line;
		if (line.indexOf(nickname + " MODE" + " " + nickname) >= 0) { // telling of mode from server when joining
			return line;
		}
		for (int i = 0; i < line.length(); i++) {
			if (!nickFound && line.substring(i, i + 1).equals(":")) {
				indexNickStart = i + 1;
				continue;
			}	
			if (!nickFound && line.substring(i, i + 1).equals("!")) {
				indexNickEnd = i;
				nickFound = true;
			}
			if (line.substring(i, i + 1).equals(":")) {
				indexMsgStart = i + 1;
				break;
			}
		}
		return "<" + line.substring(indexNickStart, indexNickEnd) + "> " + line.substring(indexMsgStart);
	}

	private class ServerListener extends AsyncTask<String, String, Void> {
		@Override
		protected Void doInBackground(String... params) {
			try {
				while ((line = read.readLine()) != null) {
					if (line.startsWith("PING ")) {
						// We must respond to PINGs to avoid being disconnected.
						write.write("PONG " + line.substring(5) + "\r\n");
						//write.write("PRIVMSG " + channel + " :I got pinged!\r\n");
						write.flush();
					}
					else { // Print input received
						if (line.indexOf(server) >= 0) { // Message from server
							publishProgress(line);
						}
						else { // Message from user
							publishProgress(processLine(line));
						}
					}
				}
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			TextView chatArea = (TextView) findViewById(R.id.textView1);
			if (chatArea.getLineCount() > 20)
				chatArea.setText("");
			chatArea.append(values[0] + "\n");
		}
	}
}