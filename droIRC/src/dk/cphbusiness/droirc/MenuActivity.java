package dk.cphbusiness.droirc;

import java.util.ArrayList;
import java.util.HashMap;

import dk.cphbusiness.droirc.datasource.DBHandler;
import dk.cphbusiness.droirc.entity.User;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MenuActivity extends Activity {

	private HashMap<String, String> serverlist; //  key = name, value = ip
	private User userFromDB;
	private DBHandler db;
	
	private void populateServerlist() {
		serverlist = new HashMap<String, String>();
		serverlist.put("Freenode", "asimov.freenode.net");
		serverlist.put("Quakenet", "jubii2.dk.quakenet.org");
		serverlist.put("Undernet", "Budapest.HU.EU.UnderNet.org");
		serverlist.put("Dalnet", "underworld.se.eu.dal.net");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		EditText editTextName = (EditText) findViewById(R.id.editText1);
				
		// DB
		db = new DBHandler(this);
		userFromDB = db.getUser(0);
		if (userFromDB != null) {
			System.err.println("Getting user (ID 0) from DB...");
			System.err.println("User loaded from from DB: " + userFromDB.getNickname());
			editTextName.setText(userFromDB.getNickname());
		}
		
		populateServerlist();
		ArrayList<String> list = new ArrayList<String>();
		for (String key : serverlist.keySet()) {
			list.add(key);
		}
		
		final ListView listview = (ListView) findViewById(R.id.listView1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);
		final Context context = this;
		listview.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EditText editNicknameText = (EditText) findViewById(R.id.editText1);
				String nickname = editNicknameText.getText().toString();
				// Check if user changed nickname, and make corresponding change in DB if so
				if (!userFromDB.getNickname().equals(nickname)) {
					db.updateUser(nickname);
				}
				db.close(); // Close DB connection
				 
			    Intent intent = new Intent(context, ChatActivity.class);
			    String selected = listview.getItemAtPosition(position).toString(); // Name of selected item in list
			    String hostip = serverlist.get(selected); // The hostname of selected item
			    
			    intent.putExtra("SERVERIP", hostip);
			    intent.putExtra("SERVERNAME", selected);
			    intent.putExtra("NICKNAME", nickname);
			    startActivity(intent);
			  }
			});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}
