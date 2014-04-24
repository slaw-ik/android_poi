package com.myroslav.mapstutorial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class HomePage extends Activity {

	TextView view;
	MarkerDataSource data;
	Context context = this;
	ProgressBar pb;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page);

		Button btnMap = (Button) findViewById(R.id.btnMap);

		view = (TextView) findViewById(R.id.textView1);
		pb = (ProgressBar) findViewById(R.id.progressBar1);

		btnMap.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						MapActivity.class);
				startActivity(intent);
			};
		});

		showDBCount();
		checkDBCount();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.mn_update_data:
			downloadCoords();
			return true;
		case R.id.mn_send_data:
			sendRatings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void sendRatings() {
		Log.d("Send Data", "TESTING");
		
		data = new MarkerDataSource(context);
		data.open();

		List<String> m = data.getAllRatings();

//		int size = m.size();
//		if (size == 0) {
//			showDownloadPrompt();
//		}
//		;

		data.close();
		
	}

	protected void downloadCoords() {
		if (isConnected()) {
			pb.setVisibility(View.VISIBLE);
			new HttpAsyncTask()
					.execute("http://poi.herokuapp.com/pointers.json");
		} else {
			Toast.makeText(getBaseContext(),
					"You are NOT conncted to the Internet!", Toast.LENGTH_LONG)
					.show();
		}

	}

	private void checkDBCount() {
		data = new MarkerDataSource(context);
		data.open();

		List<MyMarkerObj> m = data.getMyMarkers();

		int size = m.size();
		if (size == 0) {
			showDownloadPrompt();
		}
		;

		data.close();
	}

	private void showDownloadPrompt() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Empty database!");
		builder.setMessage("Database with points is empty. We recommend connect to the internet and download all coordinates.");
		builder.setCancelable(false);
		builder.setPositiveButton("Download coordinates",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						downloadCoords();
					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();

	}

	private void showDBCount() {
		data = new MarkerDataSource(context);
		data.open();

		List<MyMarkerObj> m = data.getMyMarkers();

		int size = m.size();
		view.setText(Integer.toString(size));

		data.close();
	}

	public static String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;
	}

	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return GET(urls[0]);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			pb.setVisibility(View.INVISIBLE);
			Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG)
					.show();

			JSONArray jsonArray;
			try {
				jsonArray = new JSONArray(result);

				data = new MarkerDataSource(context);
				data.open();

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject entry = jsonArray.getJSONObject(i);
					String full_desc = entry.getString("full_desc");
					Double lat = entry.getDouble("latitude");
					Double lng = entry.getDouble("longitude");
					String lat_lng = lat.toString() + " " + lng.toString();

					data.addMarker(new MyMarkerObj(full_desc, lat_lng));
				}
				data.close();
				Toast.makeText(getBaseContext(), "All data succesfully updated!", Toast.LENGTH_LONG)
				.show();
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(getBaseContext(), "Data updating Error!", Toast.LENGTH_LONG)
				.show();
			}

			showDBCount();
		}
	}

}
