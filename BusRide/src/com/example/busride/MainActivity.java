package com.example.busride;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;




public class MainActivity extends ActionBarActivity implements OnItemClickListener 
{
    public static Calendar toDate = Calendar.getInstance();
    public static Calendar fromDate = Calendar.getInstance();
    public static boolean toEdited = false;
    public static boolean fromEdited = false;
    
	public final static String EXTRA_MESSAGE = "com.example.dosearch.MESSAGE";
	
	//---Auto-complete code starts
	private static final String LOG_TAG = "ExampleApp";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";

	private static final String API_KEY = "AIzaSyCKp2QWAxwAq6OtO3zkLC-_SBLdM6TC_hE";

	public ArrayList<String> autocomplete(String input) {
	    ArrayList<String> resultList = null;

	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
	        sb.append("?sensor=false&key=" + API_KEY);
	        sb.append("&components=country:us");
	        sb.append("&types=(cities)");
	        sb.append("&input=" + URLEncoder.encode(input, "utf8"));

	        URL url = new URL(sb.toString());
	        conn = (HttpURLConnection) url.openConnection();
	        InputStreamReader in = new InputStreamReader(conn.getInputStream());
            
	        // Load the results into a StringBuilder
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	            jsonResults.append(buff, 0, read);
	        }
	    } catch (MalformedURLException e) {
	        Log.e(LOG_TAG, "Error processing Places API URL", e);
	        return resultList;
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Error connecting to Places API", e);
	        return resultList;
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }

	    try {
	        // Create a JSON object hierarchy from the results
	        JSONObject jsonObj = new JSONObject(jsonResults.toString());
	        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

	        // Extract the Place descriptions from the results
	        resultList = new ArrayList<String>(predsJsonArray.length());
	        for (int i = 0; i < predsJsonArray.length(); i++) {
	            resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
	        }
	    } catch (JSONException e) {
	        Log.e(LOG_TAG, "Cannot process JSON results", e);
	    }

	    return resultList;
	}

	 public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
	        String str = (String) adapterView.getItemAtPosition(position);
	        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	    }
	//---Auto-complete code ends
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) 
		{
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();

		}
		
		//Autcomplete code starts 
		 AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.editText1);
		 AutoCompleteTextView autoCompView2 = (AutoCompleteTextView) findViewById(R.id.editText2);
	     autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
	     autoCompView.setOnItemClickListener(this);
	     autoCompView2.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
	     autoCompView2.setOnItemClickListener(this);
	    //Autocomplete code ends    
		
		
		CheckBox repeatChkBx = ( CheckBox ) findViewById( R.id.checkBox1 );
		repeatChkBx.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
				EditText returntrip = (EditText) findViewById(R.id.editText4);
		        if ( isChecked )
		        {
		            returntrip.setVisibility(View.VISIBLE);
		        }
		        else
		        {
		        	returntrip.setVisibility(View.GONE);
		        }
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void dosearch(View view)
	{
		Intent intent = new Intent(this, DoSearch.class);
		
		EditText editText1 = (EditText) findViewById(R.id.editText1);
		String fromcity = editText1.getText().toString();
		fromcity = fromcity.replace(" ", "+"); 
		EditText editText2 = (EditText) findViewById(R.id.editText2);
		String tocity = editText2.getText().toString();
		tocity = tocity.replace(" ", "+");
		EditText editText3 = (EditText) findViewById(R.id.editText3);
		String fromdate = editText3.getText().toString();
		EditText editText4 = (EditText) findViewById(R.id.editText4);
		String todate = editText4.getText().toString();
		CheckBox checkbox = ( CheckBox ) findViewById( R.id.checkBox1 );
		Boolean radio = checkbox.isChecked();
		
		StringBuilder urlString = new StringBuilder();
		
		//urlString.append("http://murmuring-inlet-3093.herokuapp.com/rides/search.json?search[to_city]=new&search[from_city]=wash&date[to_Date]=4/23/2014&date[from_Date]=4/24/2014");
		urlString.append("http://murmuring-inlet-3093.herokuapp.com/rides/search.json?search[to_city]=" + tocity + "&search[from_city]=" + fromcity + "&date[to_Date]=" + todate + "&date[from_Date]=" + fromdate);
		System.out.println(urlString.toString());
		intent.putExtra(EXTRA_MESSAGE, urlString.toString());
		startActivity(intent);	
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{

		public PlaceholderFragment() {}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}
	
	public void showDatePickerDialog(View v) 
	{
	    DialogFragment newFragment = new DatePickerFragment((EditText) v);
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	@SuppressLint("ValidFragment")

	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener 
	{
		
		public EditText activity_edittext;

		public DatePickerFragment(EditText edit_text) 
		{
		    activity_edittext = edit_text;
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			//load previously saved to date
			if(activity_edittext.getId() == R.id.editText3)
			{
								
				if(toEdited == true)
				{
					year = toDate.get(Calendar.YEAR);
					month = toDate.get(Calendar.MONTH);
					day = toDate.get(Calendar.DAY_OF_MONTH);
				}
				
				toEdited = true;
			}
			
			//load previously saved from date
			if(activity_edittext.getId() == R.id.editText4)
			{

				if(fromEdited == true)
				{
					year = fromDate.get(Calendar.YEAR);
					month = fromDate.get(Calendar.MONTH);
					day = fromDate.get(Calendar.DAY_OF_MONTH);
				}
				
				fromEdited = true;
			}


				
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day)
		{
			
			//update to date variables
			if(activity_edittext.getId() == R.id.editText3)
			{
				toDate.set(Calendar.YEAR, year);
				toDate.set(Calendar.MONTH, month);
				toDate.set(Calendar.DAY_OF_MONTH, day);
			}
			
			
			//update from date variables
			if(activity_edittext.getId() == R.id.editText4)
			{
				fromDate.set(Calendar.YEAR, year);
				fromDate.set(Calendar.MONTH, month);
				fromDate.set(Calendar.DAY_OF_MONTH, day);
			}
			
			//make sure to date is before from date
			if(fromDate.before(toDate))
			{
				
				if(activity_edittext.getId() == R.id.editText3)
				{
				    toDate.set(fromDate.get(Calendar.YEAR), fromDate.get(Calendar.MONTH), fromDate.get(Calendar.DAY_OF_MONTH));
					year = fromDate.get(Calendar.YEAR);
				    month = fromDate.get(Calendar.MONTH);
				    day = fromDate.get(Calendar.DAY_OF_MONTH);
				}
				
				if(activity_edittext.getId() == R.id.editText4)
				{
				    fromDate.set(toDate.get(Calendar.YEAR), toDate.get(Calendar.MONTH), toDate.get(Calendar.DAY_OF_MONTH));
					year = toDate.get(Calendar.YEAR);
				    month = toDate.get(Calendar.MONTH);
				    day = toDate.get(Calendar.DAY_OF_MONTH);
				}
			}
						
			activity_edittext.setText(String.valueOf(month + 1 ) + "/" +   String.valueOf(day) + "/" + String.valueOf(year));
			
		}
	}

	
	
	
	//----Autocomplete code starts
	
	public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	    private ArrayList<String> resultList;

	    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
	        super(context, textViewResourceId);
	    }

	    @Override
	    public int getCount() {
	        return resultList.size();
	    }

	    @Override
	    public String getItem(int index) {
	        String str = resultList.get(index);
	        String str1 = str.substring(0 , (str.length() - 15) );
	        return str1;
	    }

	    @Override
	    public Filter getFilter() {
	        Filter filter = new Filter() {
	            @Override
	            protected FilterResults performFiltering(CharSequence constraint) {
	                FilterResults filterResults = new FilterResults();
	                if (constraint != null) {
	                    // Retrieve the autocomplete results.
	                    resultList = autocomplete(constraint.toString());

	                    // Assign the data to the FilterResults
	                    filterResults.values = resultList;
	                    filterResults.count = resultList.size();
	                }
	                return filterResults;
	            }

	            @Override
	            protected void publishResults(CharSequence constraint, FilterResults results) {
	                if (results != null && results.count > 0) {
	                    notifyDataSetChanged();
	                }
	                else {
	                    notifyDataSetInvalidated();
	                }
	            }};
	        return filter;
	    }
	    	    
	}

	
	/*public class PlacesAutocompleteActivity extends Activity implements OnItemClickListener {
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	     
	        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.editText1);
	        autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.activity_main));
	        autoCompView.setOnItemClickListener(this);
	    }
	    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
	        String str = (String) adapterView.getItemAtPosition(position);
	        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	    }
	    
	}*/
	
	
	//------------------------Autcomplete code ends
	
	
}
