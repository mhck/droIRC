package dk.cphbusiness.droirc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {

	private User user;
	private String server;
	private String servername;
	private Socket socket;
	private int port = 6667;
	private String line;
	private String channelName = "#droirc";
	private BufferedWriter write;
	private BufferedReader read;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		server = intent.getStringExtra("SERVERIP");
		servername = intent.getStringExtra("SERVERNAME");
		user = new User(0, intent.getStringExtra("NICKNAME"));
		setContentView(R.layout.activity_chat);
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setMovementMethod(new ScrollingMovementMethod());
		Toast.makeText(this, "Connecting to " + servername, Toast.LENGTH_SHORT).show();
		new ServerConnecter().execute(server, Integer.toString(port));
	}

	public boolean connect(String hostName, int port) {
		try {
			// Initializing connection and streams
			socket = new Socket(hostName, port);
			write = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			read = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Log on to the server.
			write.write("NICK " + user.getNickname() + "\r\n");
			write.write("USER " + user.getUserid() + " 8 * : Droirc bot 0.1\r\n");
			write.flush();

//			// Waiting for server to respond with 004 (logged in)
//			while ((line = read.readLine()) != null) {
//				System.out.println(line);
//				if (line.indexOf("004") >= 0) {
//					return true;
//				}
//				else if (line.indexOf("433") >= 0) {
//					System.out.println("Nickname is already in use.");
//					return false;
//				}
//			}
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
			if (message.equals("")) return; // If message is an empty string don't write to server. Blocks onClick from sending empty lines when clicking EditText field
			write.write("PRIVMSG " + channelName + " :" + message + "\r\n");
			TextView chatArea = (TextView) findViewById(R.id.textView1);
			chatArea.append("<" + user.getNickname() + "> " + message + "\n");
			editText.setText("");
			write.flush();
			scrollToBottom();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void scrollToBottom() {
		final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView1);
		scrollView.post(new Runnable() {
			public void run() {
				scrollView.smoothScrollTo(0, scrollView.getBottom());
			}
		});
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
			connect(params[0], Integer.parseInt(params[1]));
			joinChannel(channelName);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			//super.onPostExecute(result);
			new ServerListener().execute("");
		}
	}

	private class ServerListener extends AsyncTask<String, String, Void> {
		@Override
		protected Void doInBackground(String... params) {
			try {
				while ((line = read.readLine()) != null) {
					if (line.startsWith("PING ")) {
						// We must respond to PINGs to avoid being disconnected.
						write.write("PONG " + line.substring(5) + "\r\n");
						write.flush();
					}
					else { // Print input received
//						if (line.indexOf(server) >= 0) { // Message from server
//							publishProgress(line);
//						}
//						else { // Message from user
//							publishProgress(StringProcessor.processLine(line, server, nickname));
//							scrollToBottom();
//						}
						publishProgress(StringProcessor.processLine(line, server, user.getNickname()));
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
			chatArea.append(values[0] + "\n");
		}
	}
}