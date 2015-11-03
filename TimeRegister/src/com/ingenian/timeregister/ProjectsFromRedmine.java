package com.ingenian.timeregister;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.widget.Spinner;
import android.widget.Toast;

import com.ingenian.timeregister.model.Project;
import com.ingenian.timeregister.model.RedmineConfiguration;

class ProjectsFromRedmine extends AsyncTask<Void,Integer, Project[] > {		
	/**
	 * 
	 */
	private final TimeRegisterActivity timeRegisterActivity;
	private RedmineConfiguration redmineConfiguration;
	public ProjectsFromRedmine(TimeRegisterActivity timeRegisterActivity, RedmineConfiguration redmineConfiguration) {
		this.timeRegisterActivity = timeRegisterActivity;
		this.redmineConfiguration = redmineConfiguration;
	}
	/**
	 * Download a set of projects from redmine
	 */
	@Override
	protected Project[] doInBackground(Void... params) {
	/**
	 * The following piece of code does this invoking projects.
	 */
	
		String url = redmineConfiguration.getUrl() + "projects.json";
		//String apiKey = "eca019664f9d3e107521d6295b1ef05a039b7a69";
        DefaultHttpClient httpClient = new DefaultHttpClient();            
        HttpResponse httpResponse = null;
        HttpEntity entity = null;
        HttpGet httpRequest = new HttpGet(url);
        //httpRequest.setHeader("X-Redmine-API-Key",apiKey);
        httpRequest.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(redmineConfiguration.getUser(), redmineConfiguration.getPassword()), "UTF-8", false));
        httpRequest.setHeader("Content-Type","application/json");
        System.err.println("HTTP Request: ["+httpRequest+"]");
        ArrayList<Project> projects = null;            
        try {
			httpResponse = httpClient.execute(httpRequest);
			entity = httpResponse.getEntity();
			String responseStr = EntityUtils.toString(entity);
			System.out.println("Response is: ["+responseStr+"]");
			if(responseStr==null||responseStr.trim().length()==0) {
				//La respuesta llegó vacia, posiblemente por un error de autenticación.
				projects = new ArrayList<Project>();
			} else {
				projects = parseProjects(responseStr);
			}
			Project[] projectArray = new Project[projects.size()];
			return projects.toArray(projectArray);
		} catch (ClientProtocolException e) {				
			e.printStackTrace();
			return new Project[0];
		} catch (IOException e) {				
			e.printStackTrace();				
			return new Project[0];
		} catch (JSONException e) {
			e.printStackTrace();				
			return new Project[0];
		}				
	}
	/**
	 * Given the JSON String convert it into a set of projects. 
	 * @param responseStr
	 * @return
	 * @throws JSONException
	 */
	private ArrayList<Project> parseProjects(String responseStr) throws JSONException {
		ArrayList<Project> projects = new ArrayList<Project>();
		JSONObject reader = new JSONObject(responseStr);
		JSONArray jsonProjects = reader.getJSONArray("projects");
		for(int i = 0;i < jsonProjects.length();i++) {
			JSONObject jsonProject = jsonProjects.getJSONObject(i);
			if(jsonProject==null) {
				continue; 
			}
			Project project = new Project();
			project.setId(jsonProject.getInt("id"));
			project.setIdentifier(jsonProject.getString("identifier"));
			project.setDescription(jsonProject.getString("description"));
			project.setName(jsonProject.getString("name"));
			projects.add(project);
		}
		return projects;
	}
	@Override
	protected void onPostExecute(Project[] result) {		
		super.onPostExecute(result);
		if(result.length==0) {
			Toast.makeText(this.timeRegisterActivity, R.string.reload_fail , Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(this.timeRegisterActivity, R.string.reload_end , Toast.LENGTH_SHORT).show();
		this.timeRegisterActivity.addProjectsToSpinner(result,(Spinner)this.timeRegisterActivity.findViewById(R.id.morning_spinner));
		this.timeRegisterActivity.addProjectsToSpinner(result,(Spinner)this.timeRegisterActivity.findViewById(R.id.afternoon_spinner));
		
	}
	@Override
	protected void onPreExecute() {
		Toast.makeText(this.timeRegisterActivity, R.string.reload_start , Toast.LENGTH_SHORT).show();
	}
	
}