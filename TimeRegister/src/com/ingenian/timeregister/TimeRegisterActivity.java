package com.ingenian.timeregister;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ingenian.timeregister.model.Project;
import com.ingenian.timeregister.model.RedmineConfiguration;
import com.ingenian.timeregister.model.TimeEntry;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TimeRegisterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_register);
		initialize();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		RedmineConfiguration redmineConfiguration = RedmineConfiguration.loadFromSettings(sharedPref);
		if(!redmineConfiguration.isEmpty()) {
			AsyncTask<Void, Integer, Project[]> x = new ProjectsFromRedmineCurrent(this, redmineConfiguration);
			x.execute((Void)null);					
		} else {
			//Hay que configurarlo
			Toast.makeText(this,R.string.reload_canceled,Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this,SettingsActivity.class);
			startActivity(intent);
		}
	}
	/**
	 * Obtain components and give default values
	 */
	private void initialize() {
		/**
		 * Fecha
		 */
		TextView txtFecha = (TextView)findViewById(R.id.date_label);
		Calendar c = Calendar.getInstance();
		java.text.DateFormat dateFormat = SimpleDateFormat.getDateInstance();
		txtFecha.setText(dateFormat.format(c.getTime()));
		Button btnFecha = (Button) findViewById(R.id.date_button);
		btnFecha.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				 DialogFragment newFragment = new DatePickerFragment(TimeRegisterActivity.this);
				 newFragment.show(TimeRegisterActivity.this.getFragmentManager(), "datePicker");				
			}
		});	
		/**
		 * Enviar
		 */
		Button btnEnviar = (Button)findViewById(R.id.send_button);
		btnEnviar.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TimeRegisterActivity.this);
				RedmineConfiguration redmineConfiguration = RedmineConfiguration.loadFromSettings(sharedPref);
				/* Morning */
				Spinner morningSpinner = (Spinner)findViewById(R.id.morning_spinner);
				Project morningProject = (Project) morningSpinner.getSelectedItem();
				/* Afternoon */
				Spinner afternoonSpinner = (Spinner)findViewById(R.id.afternoon_spinner);				
				Project afternoonProject = (Project) afternoonSpinner.getSelectedItem();;
				/* Date */
				Date date = extractDateFromComponent();
				TimeEntry timeEntry = new TimeEntry(morningProject, afternoonProject, date);
				TimeEntryToRedmine x = new TimeEntryToRedmine(TimeRegisterActivity.this, redmineConfiguration);
				x.execute(timeEntry);				
			}

			private Date extractDateFromComponent() {
				TextView txtFecha = (TextView)findViewById(R.id.date_label);
				java.text.DateFormat dateFormat = SimpleDateFormat.getDateInstance();
				Date date;
				try {
					date = dateFormat.parse(txtFecha.getText().toString());
				} catch (ParseException e) {
					e.printStackTrace();
					date = Calendar.getInstance().getTime();					
				}
				return date;
			}
		});
		//Ring at 6PM
		// Get the AlarmManager Service
				AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

				// Create an Intent to broadcast to the AlarmNotificationReceiver
				Intent mNotificationReceiverIntent = new Intent(this,
						AlarmNotificationReceiver.class);

				// Create an PendingIntent that holds the NotificationReceiverIntent
				PendingIntent mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
						this, 0, mNotificationReceiverIntent, 0);
				 Calendar calendar = Calendar.getInstance();
		           calendar.set(Calendar.HOUR_OF_DAY, 18);
		       calendar.set(Calendar.MINUTE, 00);
		       calendar.set(Calendar.SECOND, 00);

		      mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000 , mNotificationReceiverPendingIntent);

	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id) {
		case R.id.menu_settings: 
			Intent intent = new Intent(this,SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_reload:
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			
			AsyncTask<Void, Integer, Project[]> x = new ProjectsFromRedmineCurrent(this, RedmineConfiguration.loadFromSettings(sharedPref));
			x.execute((Void)null);	
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	void addProjectsToSpinner(Project[] projects, Spinner spinner) {
		ArrayAdapter<Project> adapter = new ArrayAdapter<Project>(this, android.R.layout.simple_spinner_item,projects);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
}
