package com.example.busride;

import java.util.Calendar;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "com.example.dosearch.MESSAGE";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
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
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
	public void dosearch(View view){
		Intent intent = new Intent(this, DoSearch.class);
		
		EditText editText1 = (EditText) findViewById(R.id.editText1);
		String fromcity = editText1.getText().toString();
		EditText editText2 = (EditText) findViewById(R.id.editText2);
		String tocity = editText2.getText().toString();
		EditText editText3 = (EditText) findViewById(R.id.editText3);
		String fromdate = editText3.getText().toString();
		EditText editText4 = (EditText) findViewById(R.id.editText4);
		String todate = editText4.getText().toString();
		CheckBox checkbox = ( CheckBox ) findViewById( R.id.checkBox1 );
		Boolean radio = checkbox.isChecked();
		
		StringBuilder urlString = new StringBuilder();
		//urlString.append("http://murmuring-inlet-3093.herokuapp.com/rides/search.json?search[to_city]=new&search[from_city]=wash&date[to_Date]=4/23/2014&date[from_Date]=4/24/2014");
		urlString.append("http://murmuring-inlet-3093.herokuapp.com/rides/search.json?search[to_city]=" + tocity + "&search[from_city]=" + fromcity + "&date[to_Date]=" + todate + "&date[from_Date]=" + fromdate);
		
		intent.putExtra(EXTRA_MESSAGE, urlString.toString());
		startActivity(intent);	
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment((EditText) v);
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	@SuppressLint("ValidFragment")
	public static class DatePickerFragment extends DialogFragment 
								implements DatePickerDialog.OnDateSetListener {
		
		public EditText activity_edittext;

		public DatePickerFragment(EditText edit_text) {
		    activity_edittext = edit_text;
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			activity_edittext.setText(String.valueOf(month + 1 ) + "/" +   String.valueOf(day) + "/" + String.valueOf(year));
		}
	}

}
