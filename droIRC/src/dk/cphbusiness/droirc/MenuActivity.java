package dk.cphbusiness.droirc;

import java.util.ArrayList;
import java.util.HashMap;

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
		populateServerlist();
		final ListView listview = (ListView) findViewById(R.id.listView1);

		ArrayList<String> list = new ArrayList<String>();
		for (String key : serverlist.keySet()) {
			list.add(key);
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);
		final Context context = this;
		listview.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    Intent intent = new Intent(context, ChatActivity.class);
			    String selected = listview.getItemAtPosition(position).toString(); // Name of selected item in list
			    String hostip = serverlist.get(selected); // The hostname of selected item
			    EditText editNicknameText = (EditText) findViewById(R.id.editText1);
			    String nickname = editNicknameText.getText().toString();
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
