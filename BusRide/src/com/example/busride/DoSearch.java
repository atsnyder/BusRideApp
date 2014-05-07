package com.example.busride;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.ActionBar.TabListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.net.HttpURLConnection;
import java.net.URL;

public class DoSearch extends FragmentActivity implements ActionBar.TabListener, TabListener {

	private static String URL = null;
	private static String returnURL = null;
	private static boolean radio = false;
	private static boolean secondsearch = false;
	
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private android.app.ActionBar actionBar;
	private View v;
	
	// Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
	
	private String[] tabs = {"Depart", "Return"};
	private ProgressDialog progress = null;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_do_search);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String[] msgparts = message.split("THECODEISSTRONG");
        String[] moarmsgparts = msgparts[1].split("YOUSEENOTHING");
        URL = msgparts[0];
        returnURL = moarmsgparts[1];
        
        if(moarmsgparts[0].equals("RoundTrip")){
        	radio = true;
		
			viewPager = (ViewPager) findViewById(R.id.pager);
	        actionBar = getActionBar();
	        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
	 
	        viewPager.setAdapter(mAdapter);
	        actionBar.setHomeButtonEnabled(false);
	        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			
	        for (String tab_name : tabs) {
	            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
	        }
			
			viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
	
				@Override
				public void onPageSelected(int position) {
					// on changing the page
					// make respected tab selected
					actionBar.setSelectedNavigationItem(position);
				}
	
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}
	
				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
		
        }
        else{
        	setContentView(R.layout.activity_one_way);
        	radio = false;
        }
        
		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
	}
	
    @Override
    public void onStart() {
        super.onStart();

        /*Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String[] msgparts = message.split("THECODEISSTRONG");
        URL = msgparts[0];
        
        if(msgparts[1].equals("ReturnTrip")){
        	radio = true;
        }*/
        

        
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();

        secondsearch = false;
        
        loadPage();
        //new DownloadXmlTask().execute(URL);

        
        //progress.dismiss();
        //TextView textview = (TextView) findViewById(R.id.textView1);
        //textview.setText(message);
        // = message;
        
    }
    
    private void loadPage() {
    	ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
        if (wifiConnected || mobileConnected )
        {
        	new DownloadXmlTask().execute(URL);
        	if (radio == true){
        		//secondsearch = true;
        		new DownloadXmlTask().execute(returnURL);
        	}
        } else {
            showErrorPage();
        }
    }
    
    private void showErrorPage() {
    	Context context = getApplicationContext();
    	CharSequence text = "Unable to load content. Check your network connection.";
    	int duration = Toast.LENGTH_LONG;

    	Toast toast = Toast.makeText(context, text, duration);
    	toast.setGravity(Gravity.TOP, 0, 350);
    	toast.show();
        super.finish();
    }

	/*@Override
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
	}*/

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
            	return getResources().getString(R.string.connection_error);
            } catch (JSONException e) {
            	return getResources().getString(R.string.json_error);
			}
        }

        @Override
        protected void onPostExecute(String result) {
            //setContentView(R.layout.activity_do_search);
            if (radio == false){
            	setContentView(R.layout.activity_one_way);
            }
            //System.out.println(result);
            TableLayout tlay = (TableLayout) findViewById(R.id.maintable);
        	if (secondsearch) {
        		tlay = (TableLayout) findViewById(R.id.rettable);
        		secondsearch = false;
        	}
        	else {
        		tlay = (TableLayout) findViewById(R.id.maintable);
        		secondsearch = true;
        	}
            
            String[] rows = result.split("SECRETCODE");

            for (int i = 0; i<rows.length; i++){ 
            	
            	String[] cols = rows[i].split("NOTSOCODE");
            	
            	if(cols.length != 7)
            	{
            		Context context = getApplicationContext();
                	CharSequence text = "No results for that day.";
                	int duration = Toast.LENGTH_LONG;

                	Toast toast = Toast.makeText(context, text, duration);
                	toast.setGravity(Gravity.TOP, 0, 350);
                	toast.show();
                    finish();
            		return;
            	}
            	
            	View view = getLayoutInflater().inflate(R.layout.new_table_row,tlay,false);
            	
            	TextView price = (TextView) view.findViewById(R.id.tCol21);
            	price.setText("$" + cols[0]);
            	
            	TextView depcity = (TextView) view.findViewById(R.id.depcity);
            	depcity.setText("From: " + cols[1]);
            	
            	TextView arrcity = (TextView) view.findViewById(R.id.arrcity);
            	arrcity.setText("To: " + cols[2]);
            	
            	CharSequence formattime = cols[3].subSequence(0,16);
            	CharSequence fmonth = formattime.subSequence(5,7);
            	CharSequence fyear = formattime.subSequence(0,4);
            	CharSequence fday = formattime.subSequence(8,10);
            	CharSequence fhour = formattime.subSequence(11,13);
            	CharSequence fminute = formattime.subSequence(14,16);
            	String smonth = fmonth.toString();
            	String syear = fyear.toString();
            	String sday = fday.toString();
            	String sminute = fminute.toString();
            	String shour = fhour.toString();
            	int ihour = Integer.parseInt(shour);
            	int imonth = Integer.parseInt(smonth);
            	
            	String ampm = null;
            	if (ihour < 12){
            		ampm = "AM";
            	}
            	else {
            		ampm = "PM";
            	}
            	
            	if(ihour == 0){
            		ihour = 12;
            	}
            	else if (ihour > 12){
            		ihour = ihour - 12;
            	}
            	
            	String finmonth = null;
            	switch (imonth) {
            	case 1: finmonth = "Jan";
            		break;
            	case 2: finmonth = "Feb";
        			break;
            	case 3: finmonth = "Mar";
        			break;
            	case 4: finmonth = "Apr";
        			break;
            	case 5: finmonth = "May";
        			break;
            	case 6: finmonth = "Jun";
        			break;
            	case 7: finmonth = "Jul";
        			break;
            	case 8: finmonth = "Aug";
        			break;
            	case 9: finmonth = "Sep";
        			break;
            	case 10: finmonth = "Oct";
        			break;
            	case 11: finmonth = "Nov";
        			break;
            	case 12: finmonth = "Dec";
        			break;
    			default: finmonth = "dungoofed";
    				break;
            	}
       
            	TextView deptime = (TextView) view.findViewById(R.id.deptime);
            	deptime.setText("Depart: " + String.valueOf(ihour) + ":" + sminute + ampm + " " + finmonth + " " + sday + ", " + syear);
            	
            	formattime = cols[4].subSequence(0,16);
            	fmonth = formattime.subSequence(5,7);
            	fyear = formattime.subSequence(0,4);
            	fday = formattime.subSequence(8,10);
            	fhour = formattime.subSequence(11,13);
            	fminute = formattime.subSequence(14,16);
            	smonth = fmonth.toString();
            	syear = fyear.toString();
            	sday = fday.toString();
            	sminute = fminute.toString();
            	shour = fhour.toString();
            	ihour = Integer.parseInt(shour);
            	imonth = Integer.parseInt(smonth);
            	
            	if (ihour < 12){
            		ampm = "AM";
            	}
            	else {
            		ampm = "PM";
            	}
            	
            	if(ihour == 0){
            		ihour = 12;
            	}
            	else if (ihour > 12){
            		ihour = ihour - 12;
            	}
            	
            	switch (imonth) {
            	case 1: finmonth = "Jan";
            		break;
            	case 2: finmonth = "Feb";
        			break;
            	case 3: finmonth = "Mar";
        			break;
            	case 4: finmonth = "Apr";
        			break;
            	case 5: finmonth = "May";
        			break;
            	case 6: finmonth = "Jun";
        			break;
            	case 7: finmonth = "Jul";
        			break;
            	case 8: finmonth = "Aug";
        			break;
            	case 9: finmonth = "Sep";
        			break;
            	case 10: finmonth = "Oct";
        			break;
            	case 11: finmonth = "Nov";
        			break;
            	case 12: finmonth = "Dec";
        			break;
    			default: finmonth = "dungoofed";
    				break;
            	}
            	
            	TextView arrtime = (TextView) view.findViewById(R.id.arrtime);
            	arrtime.setText("Arrive: " + String.valueOf(ihour) + ":" + sminute + ampm + " " + finmonth + " " + sday + ", " + syear);
            	
            	TextView company = (TextView) view.findViewById(R.id.company);
            	company.setText("Company: " + cols[6]);
            	
            	//removed from new_table_row
            	/*<Button
                android:id="@+id/urlbutton"
                style="?android:attr/buttonStyleSmall"
                android:layout_width="wrap_content"
                android:layout_height="34dp"
                android:text="View" />*/
            	
            	Button button= new Button(getApplicationContext());
            	button.setId(i);
            	button.setText("View");
            	final String url = cols[5]; 
            	button.setOnClickListener(new View.OnClickListener()   
            	{
            	    public void onClick(View view) 
            	     {
            	    	startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
            	       }
            	});
            	LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            	
            	Context con = view.getContext();
            	
                v = new View(con);
                v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1));
                v.setBackgroundColor(Color.rgb(51, 51, 51));
            	
            	tlay.addView(view);
            	tlay.addView(button, lp);
            	tlay.addView(v);
            }
            progress.dismiss();
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
			result.append(ride.getString("TRIP_COST") + "NOTSOCODE" + ride.getString("DEPART_CITY") + "NOTSOCODE" + ride.getString("ARRIVE_CITY") + "NOTSOCODE" + ride.getString("DEPART_TIME") + "NOTSOCODE" + ride.getString("ARRIVE_TIME") + "NOTSOCODE" + ride.getString("URL") + "NOTSOCODE" + ride.getString("COMPANY_NAME") + "SECRETCODE");
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

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(android.app.ActionBar.Tab tab,
			android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(android.app.ActionBar.Tab tab,
			android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(android.app.ActionBar.Tab tab,
			android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}
