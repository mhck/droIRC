package dk.cphbusiness.droirc;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
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
		ListView listview = (ListView) findViewById(R.id.listView1);

		ArrayList<String> list = new ArrayList<String>();
		for (String key : serverlist.keySet()) {
			list.add(key);
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}
