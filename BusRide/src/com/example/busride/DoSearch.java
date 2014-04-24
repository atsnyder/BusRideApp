package com.example.busride;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.os.Build;
import android.preference.PreferenceManager;

import java.net.HttpURLConnection;
import java.net.URL;

public class DoSearch extends ActionBarActivity {

	private static String URL = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_do_search);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
    @Override
    public void onStart() {
        super.onStart();

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        URL = message;
        
        new DownloadXmlTask().execute(URL);
        
        //TextView textview = (TextView) findViewById(R.id.textView1);
        //textview.setText(message);
        // = message;
        
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.do_search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_do_search,
					container, false);
			return rootView;
		}
	}
	
    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadJsonFromNetwork(urls[0]);
            } catch (IOException e) {
                return getResources().getString(1);
            } catch (JSONException e) {
				e.printStackTrace();
			}
			return URL;
        }

        @Override
        protected void onPostExecute(String result) {
            setContentView(R.layout.activity_do_search);
            
            System.out.println(result);
            
            String[] rows = result.split("/");
            String[] items = rows[0].split(":");
            
            TextView textview1 = (TextView) findViewById(R.id.tCol11);
			textview1.setText(items[0]);
			TextView textview2 = (TextView) findViewById(R.id.tCol12);
			textview2.setText(items[1]);
			TextView textview3 = (TextView) findViewById(R.id.tCol13);
			textview3.setText(items[2]);
            //TextView textview = (TextView) findViewById(R.id.textView1);
            //textview.setText(result);
        }
    }
    
    private String loadJsonFromNetwork(String urlString) throws IOException, JSONException {
		InputStream stream = null;
		String jsonString = null;
    	
		StringBuilder result = new StringBuilder();
		
		stream = downloadUrl(urlString);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		while ((line = reader.readLine()) != null){
			sb.append(line+"\n");
		}
		
		jsonString = sb.toString();
		
		JSONArray jArray = new JSONArray(jsonString);
		
		for (int i = 0; i < jArray.length(); i++){
			JSONObject oneObject = jArray.getJSONObject(i);
			JSONObject ride = oneObject.getJSONObject("ride");
			result.append(ride.getString("DEPART_CITY") + ":" + ride.getString("ARRIVE_CITY") + ":" + ride.getString("DEPART_TIME") + "/");
			/*TextView textview1 = (TextView) findViewById(R.id.tCol11);
			textview1.setText(ride.getString("DEPART_CITY"));
			TextView textview2 = (TextView) findViewById(R.id.tCol12);
			textview2.setText(ride.getString("ARRIVE_CITY"));
			TextView textview3 = (TextView) findViewById(R.id.tCol13);
			textview3.setText(ride.getString("ARRIVE_TIME"));*/
		}
		
		if(stream != null){
			stream.close();
		}
		
    	return result.toString();
    	
    }
    
    private InputStream downloadUrl(String urlString) throws IOException {
		URL url = new URL(urlString);
    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	conn.setReadTimeout(10000);
    	conn.setConnectTimeout(15000);
    	conn.setRequestMethod("GET");
    	conn.setDoInput(true);
    	
    	conn.connect();
    	InputStream stream = conn.getInputStream();
    	
    	return stream;
    }
}
