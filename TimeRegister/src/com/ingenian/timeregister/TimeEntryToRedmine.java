package com.ingenian.timeregister;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.widget.Toast;

import com.ingenian.timeregister.model.RedmineConfiguration;
import com.ingenian.timeregister.model.TimeEntry;

class TimeEntryToRedmine extends AsyncTask<TimeEntry, Integer, Boolean> {
	/**
	 * 
	 */
	private final TimeRegisterActivity timeRegisterActivity;
	private RedmineConfiguration redmineConfiguration;
	public TimeEntryToRedmine(TimeRegisterActivity timeRegisterActivity, RedmineConfiguration redmineConfiguration) {
		this.timeRegisterActivity = timeRegisterActivity;
		this.redmineConfiguration = redmineConfiguration;
	}
	/** 
	 * Connect to Redmine and send a TimeEntry their way
	 */
	@Override
	protected Boolean doInBackground(TimeEntry... params) {
		for (TimeEntry timeEntry : params) {
			for (String singleTimeEntry : timeEntry.toJSON()) {
				String url = redmineConfiguration.getUrl()+"time_entries.json";
				DefaultHttpClient httpClient = new DefaultHttpClient();            
				HttpResponse httpResponse = null;	        
				HttpPost httpRequest = new HttpPost(url);
				StringBuffer timeEntryBuffer = new StringBuffer();
				HttpEntity entity;
				timeEntryBuffer.append("{\"time_entry\": ");	       	       
				timeEntryBuffer.append(singleTimeEntry);
				timeEntryBuffer.append("}");
				String x = timeEntryBuffer.toString();
				try {
		        	entity = new StringEntity(x);	        
		        	httpRequest.setEntity(entity);
		        	//httpRequest.setHeader("X-Redmine-API-Key",apiKey);
		        	httpRequest.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(redmineConfiguration.getUser(), redmineConfiguration.getPassword()), "UTF-8", false));
		        	httpRequest.setHeader("Content-Type","application/json");
		        	System.err.println("The HTTP Request: "+url+"["+x+"]");
					httpResponse = httpClient.execute(httpRequest);
					entity = httpResponse.getEntity();
					String responseStr = EntityUtils.toString(entity);
					System.err.println("Response is: ["+responseStr+"]"+httpResponse.getStatusLine());				
				} catch (ClientProtocolException e) {				
					e.printStackTrace();
					return false;
				} catch (IOException e) {				
					e.printStackTrace();				
					return false;
				}
			}
		}
		return true;
	}
	@Override
	protected void onPreExecute() {			
		super.onPreExecute();
		Toast.makeText(this.timeRegisterActivity, R.string.commit_start, Toast.LENGTH_SHORT).show();
	}
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		Toast.makeText(this.timeRegisterActivity, (result?R.string.commit_end:R.string.commit_fail), Toast.LENGTH_LONG).show();			
	}
}