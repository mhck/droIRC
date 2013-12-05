package dk.cphbusiness.droirc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import android.app.Activity;
import android.app.FragmentManager;
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

public class ChatActivity extends Activity implements ServerListenerFragment.TaskCallbacks {

	private User user;
	private String server;
	private String servername;
	private Socket socket;
	private int port = 6667;
	private ArrayList<String> channels;
	private String line;
	private String channelName = "#droirc";
	private BufferedWriter write;
	private BufferedReader read;
	private ServerListenerFragment serverListenerFragment;
	private FragmentManager serverListenerManager;
	private boolean resumed = true;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		// Test channels to show
		channels = new ArrayList<String>();
		channels.add("#droirc");
		channels.add("#linux");
		channels.add("#linux.dk");
		channels.add("#c++");
		channels.add("#android-dev");
		
		// Information from startmenu activity
		Intent intent = getIntent();
		server = intent.getStringExtra("SERVERIP");
		servername = intent.getStringExtra("SERVERNAME");
		user = new User(0, intent.getStringExtra("NICKNAME"));
				
		// Saves user to DB
		DBHandler db = new DBHandler(this);
		User userFromDb = db.getUser(0);
		if (userFromDb != null) {
			System.err.println("Getting user (ID 0) from DB...");
			System.err.println("User loaded from from DB: " + userFromDb.getNickname());
		}
		else {
			System.err.println("User not found in DB... Adding current user...");
			db.addUser(user);
			System.err.println("User " + user.getNickname() + " added to DB...");
			System.err.println("Testing to see if user exists in DB: ");
			System.err.println("Loaded ID 0: " + db.getUser(0).getNickname());
		}
		
		// Fragment Manager (to interact with server listener)
		serverListenerManager = getFragmentManager();
		serverListenerFragment = (ServerListenerFragment) serverListenerManager.findFragmentByTag("listenertask");
		
		// If fragment is null create new
		if (serverListenerFragment == null) {
			resumed = false;
			serverListenerFragment = new ServerListenerFragment();
			serverListenerManager.beginTransaction().add(serverListenerFragment, "listenertask").commit();
		}
		
		
		if (!resumed) { // Only connect if new instance of program
			Toast.makeText(this, "Connecting to " + servername, Toast.LENGTH_SHORT).show();
			new ServerConnecter().execute(server, Integer.toString(port));
		}
		else {
			serverListenerManager.beginTransaction().commit();
		}
		
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setMovementMethod(new ScrollingMovementMethod());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("serverip", server);
		outState.putString("nickname", user.getNickname());
		
	}
	
	@Override
	public void onProgressUpdate(String... values) {
		TextView chatArea = (TextView) findViewById(R.id.textView1);
		chatArea.append(values[0] + "\n");
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
			channels.add(channelName);
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
			if (message.length() > 7 && message.substring(0, 5).equalsIgnoreCase("/join")) { // check if user writes /join
				joinChannel(message.substring(6));
			}
			else {
				write.write("PRIVMSG " + channelName + " :" + message + "\r\n");
				TextView chatArea = (TextView) findViewById(R.id.textView1);
				chatArea.append("<" + user.getNickname() + "> " + message + "\n");
				editText.setText("");
				write.flush();
			} 
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

	private class ServerConnecter extends AsyncTask<String, String, Void> {		
		@Override
		protected Void doInBackground(String... params) {
			connect(params[0], Integer.parseInt(params[1]));
			joinChannel(channelName);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			serverListenerManager.beginTransaction().commit();
		}
	}
	
	public User getUser() {
		return user;
	}

	public BufferedWriter getWriter() {
		return write;
	}

	public BufferedReader getReader() {
		return read;
	}
	
	public String getLine() {
		return line;
	}
	
	public void setLine(String line) {
		this.line = line;
	}
	
	public String getServerIp() {
		return server;
	}

	@Override
	public void onPreExecute() {

	}

	@Override
	public void onCancelled() {

	}

	@Override
	public void onPostExecute() {
	
	}
	
}