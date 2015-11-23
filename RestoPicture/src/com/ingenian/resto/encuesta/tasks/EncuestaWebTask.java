package com.ingenian.resto.encuesta.tasks;

import java.io.IOException;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ingenian.resto.encuesta.R;
import com.ingenian.resto.encuesta.model.Encuesta;
import com.ingenian.resto.encuesta.model.RestoConfiguration;
/**
 * This sends a single Encuesta to the world
 * @author Juan Pablo Morales
 *
 */
public class EncuestaWebTask extends AsyncTask<Encuesta,Integer, Boolean > {
	private static String TAG="ENCUESTA_WEB_TASK";	
	private WeakReference<Activity> mParent;
	private RestoConfiguration restoConfiguration;
	public EncuestaWebTask(Activity activity, RestoConfiguration restoConfiguration) {
		this.mParent = new WeakReference<Activity>(activity);
		this.restoConfiguration = restoConfiguration;
	}
	/**
	 * Upload. 
	 * Returns true if it worked, false if it did not. 
	 */
	@Override
	protected Boolean doInBackground(Encuesta... params) {
	/**
	 * The following piece of code does this invoking Meseros.
	 */	
		if(params.length!=1) {
			Log.w(TAG,"Doing nothing in background. Received "+params.length+ " encuestas.");
			return false;
		}
		String url = restoConfiguration.getUrl() + "api/encuestas";
		DefaultHttpClient httpClient = new DefaultHttpClient();            
        HttpResponse httpResponse = null;
        HttpEntity entity = null;
        HttpPost httpRequest = new HttpPost(url);        
        //httpRequest.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(restoConfiguration.getUser(), restoConfiguration.getPassword()), "UTF-8", false));       
               
                    
        try {
        	String encuestaJSON = params[0].toJSON().toString();
        	Log.d(TAG,"Enviando JSON: "+encuestaJSON);
        	httpRequest.setEntity(new StringEntity(encuestaJSON));
        	httpRequest.setHeader("Accept", "application/json");
        	httpRequest.setHeader("Content-type", "application/json");
			httpResponse = httpClient.execute(httpRequest);
			entity = httpResponse.getEntity();
			String responseStr = EntityUtils.toString(entity);
			System.out.println("Response is: ["+responseStr+"]");
			if(responseStr==null||responseStr.trim().length()==0) {
				//La respuesta llegó vacia, posiblemente por un error de autenticación.
				return false;
			} else {
				return true;
			}						
        } catch(JSONException e) {
        	e.printStackTrace();
        	
		}  catch (ClientProtocolException e) {				
			e.printStackTrace();
		//	return new ArrayList<Mesero>();
		} catch (IOException e) {				
			e.printStackTrace();				
		//	return new ArrayList<Mesero>();
		}
        return false;
	}
	@Override
	protected void onPostExecute(Boolean result) {		
		super.onPostExecute(result);		
		if(!result) {
			Toast.makeText(mParent.get(), R.string.encuesta_error , Toast.LENGTH_SHORT).show();			
		} else {
			Toast.makeText(mParent.get(), R.string.encuesta_done , Toast.LENGTH_SHORT).show();	
		}	
	
	}
	@Override
	protected void onPreExecute() {
		Toast.makeText(mParent.get(), R.string.encuesta_load , Toast.LENGTH_SHORT).show();
	}
	
}