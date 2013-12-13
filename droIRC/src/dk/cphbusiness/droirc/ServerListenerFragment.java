package dk.cphbusiness.droirc;

import java.io.IOException;

import dk.cphbusiness.droirc.util.StringProcessor;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

public class ServerListenerFragment extends Fragment {
	private TaskCallbacks callbacks;
	private ListenerTask task;
	private ChatActivity chat;

	static interface TaskCallbacks {
		void onPreExecute();
		void onProgressUpdate(String... values);
		void onCancelled();
		void onPostExecute();
	}

	/**
	 * Hold a reference to the parent Activity so we can report the
	 * task's current progress and results. The Android framework
	 * will pass us a reference to the newly created Activity after
	 * each configuration change.
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callbacks = (TaskCallbacks) activity;
	}

	/**
	 * This method will only be called once when the retained
	 * Fragment is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Getting the parent activity
		chat = (ChatActivity) getActivity();
		
		// Retain this fragment across configuration changes.
		setRetainInstance(true);

		// Create and execute the background task.
		task = new ListenerTask();
		task.execute();
	}

	/**
	 * Set the callback to null so we don't accidentally leak the
	 * Activity instance.
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		callbacks = null;
	}

	/**
	 * A dummy task that performs some (dumb) background work and
	 * proxies progress updates and results back to the Activity.
	 *
	 * Note that we need to check if the callbacks are null in each
	 * method in case they are invoked after the Activity's and
	 * Fragment's onDestroy() method have been called.
	 */
	private class ListenerTask extends AsyncTask<Void, String, Void> {

		@Override
		protected void onPreExecute() {
			if (callbacks != null) {
				callbacks.onPreExecute();
			}
		}

		/**
		 * Note that we do NOT call the callback object's methods
		 * directly from the background thread, as this could result
		 * in a race condition.
		 */
		@Override
		protected Void doInBackground(Void... ignore) {
			try {
				chat.getConnection().setLine(chat.getConnection().getReader().readLine()); // reads line
				while (chat.getConnection().getLine() != null) {
					if (chat.getConnection().getLine().startsWith("PING ")) {
						// We must respond to PINGs to avoid being disconnected.
						chat.getConnection().getWriter().write("PONG " + chat.getConnection().getLine().substring(5) + "\r\n");
						chat.getConnection().getWriter().flush();
					}
					else {
						publishProgress(StringProcessor.processLine(chat.getConnection().getLine(), chat.getConnection().getHostName(), chat.getConnection().getUser().getNickname()));
					}
					chat.getConnection().setLine(chat.getConnection().getReader().readLine());
				}
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			return null;
		}

		protected void onProgressUpdate(String... values) {
			/*super.onProgressUpdate(values);
			TextView chatArea = (TextView) chat.findViewById(R.id.textView1);
			chatArea.append(values[0] + "\n");*/
			chat.onProgressUpdate(values);
		}

		@Override
		protected void onCancelled() {
			if (callbacks != null) {
				callbacks.onCancelled();
			}
		}

		@Override
		protected void onPostExecute(Void ignore) {
			if (callbacks != null) {
				callbacks.onPostExecute();
			}
		}
	}
}