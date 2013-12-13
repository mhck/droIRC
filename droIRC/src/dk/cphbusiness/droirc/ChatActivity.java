package dk.cphbusiness.droirc;

import java.io.IOException;

import dk.cphbusiness.droirc.entity.Connection;
import dk.cphbusiness.droirc.entity.User;
import dk.cphbusiness.droirc.util.SaveConfiguration;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends FragmentActivity implements ServerListenerFragment.TaskCallbacks {

	private Connection connection;
	private ServerListenerFragment serverListenerFragment;
	private FragmentManager serverListenerManager;
	private boolean resumed = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		// Local variables used for ServerConnecter
		String serverip = null, servername = null; 
		User user;
		
		final SaveConfiguration save = (SaveConfiguration) getLastCustomNonConfigurationInstance();
		if (save == null) {
			// Information from startmenu activity
			Intent intent = getIntent();
			serverip = intent.getStringExtra("SERVERIP");
			servername = intent.getStringExtra("SERVERNAME");
			user = new User(0, intent.getStringExtra("NICKNAME"));
		}
		else { // String hostName, String chatText, String channelName, User user, BufferedWriter write, BufferedReader read, Socket socket
			System.err.println("Loading information from SaveConfiguration..");
			TextView textViewChat = (TextView) findViewById(R.id.textView1);
			textViewChat.setText(save.getChatText());
			serverip = save.getConnection().getHostName();
			user = save.getUser();
			connection = save.getConnection();
			connection.setWrite(save.getConnection().getWriter());
			connection.setRead(save.getConnection().getReader());
			connection.setSocket(save.getConnection().getSocket());
			System.err.println("SaveConfiguration loaded succesfully..");
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
			String[] serverInfo = { serverip, String.valueOf(6667), user.getNickname() };
			new ServerConnecter().execute(serverInfo);
			//new ServerConnecter().execute(serverip, Integer.toString(port));
		}
		else {
			serverListenerManager.beginTransaction().commit();
		}
		
		// Attach scroll to textview
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setMovementMethod(new ScrollingMovementMethod());
	}
	
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		TextView textViewChat = (TextView) findViewById(R.id.textView1);
		String chatText = textViewChat.getText().toString();
		final SaveConfiguration save = new SaveConfiguration(connection, chatText, connection.getUser());
		return save;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case R.id.action_disconnect:
            connection.disconnect();
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
//		outState.putString("serverip", server);
//		outState.putString("nickname", user.getNickname());
	}
	
	@Override
	public void onProgressUpdate(String... values) {
		TextView chatArea = (TextView) findViewById(R.id.textView1);
		chatArea.append(values[0] + "\n");
		scrollToBottom();
	}

	public void message(View view) {
		try {			
			EditText editText = (EditText) findViewById(R.id.editText1);
			String message = editText.getText().toString();
			if (message.equals("")) return; // If message is an empty string don't write to server. Blocks onClick from sending empty lines when clicking EditText field
			if (message.length() > 7 && message.substring(0, 5).equalsIgnoreCase("/join")) { // check if user writes /join
				String channel = message.substring(6);
				connection.joinChannel(channel);
				TextView chatArea = (TextView) findViewById(R.id.textView1);
				chatArea.append("Joined channel " + channel + "\n");
				editText.setText("");
				connection.getWriter().flush();
			}
			else {
				TextView chatArea = (TextView) findViewById(R.id.textView1);
				if (connection.getChannels().size() == 0)
					chatArea.append("You need to join a channel before chatting!");
				connection.getWriter().write("PRIVMSG " + connection.getChannels().get(0) + " :" + message + "\r\n");
				chatArea.append("<" + connection.getUser().getNickname() + "> " + message + "\n");
				editText.setText("");
				connection.getWriter().flush();
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
				scrollView.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	private class ServerConnecter extends AsyncTask<String, String, Void> {		
		@Override
		protected Void doInBackground(String... params) {
			//connect(params[0], Integer.parseInt(params[1]));
			String hostName = params[0];
			int port = Integer.parseInt(params[1]);
			User user = new User(0, params[2]);
			connection = new Connection(hostName, port, user);
			connection.connect();
			return null;
		}
		
		

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			serverListenerManager.beginTransaction().commit();
		}
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
	
	public Connection getConnection() {
		return connection;
	}
	
//	private class DrawerItemClickListener implements OnItemClickListener {
//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
//		selectItem(pos);
//	}
//	
//	private void selectItem(int pos) {
		// Create new fragment and specify the channel to show based on pos
//		Fragment fragment = new ServerListenerFragment();
//		Bundle args = new Bundle();
//		args.putInt("test", pos);
//		fragment.setArguments(args);
//		
//		// Insert fragment by replacing any existing fragment
//		FragmentManager fManager = getFragmentManager();
//		fManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//		
//		// Highlight the selected item, update the title, and close the drawer
//	    drawerList.setItemChecked(pos, true);
//		setTitle(mPlanetTitles[position]);
//	    drawerLayout.closeDrawer(drawerList);
//	}
//
//}
	
}