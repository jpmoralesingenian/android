package com.ingenian.resto.encuesta.tasks;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ingenian.resto.encuesta.R;
import com.ingenian.resto.encuesta.activities.EncuestaActivity;
import com.ingenian.resto.encuesta.model.Mesero;
import com.ingenian.resto.encuesta.model.RestoConfiguration;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * This gets the list of all Meseros for the current user. It uses the current
 * @author Juan Pablo Morales
 *
 */
public class MeserosWebTask extends AsyncTask<Void,Integer, ArrayList<Mesero> > {		
	private static Bitmap homero;
	private WeakReference<EncuestaActivity> mParent;
	private RestoConfiguration restoConfiguration;
	public MeserosWebTask(EncuestaActivity encuestaActivity, RestoConfiguration restoConfiguration) {
		this.mParent = new WeakReference<EncuestaActivity>(encuestaActivity);
		this.restoConfiguration = restoConfiguration;
	}
	/**
	 * Download a set of Meseros from redmine
	 */
	@Override
	protected ArrayList<Mesero> doInBackground(Void... params) {
	/**
	 * The following piece of code does this invoking Meseros.
	 */
	
		String url = restoConfiguration.getUrl() + "api/meseros";
		//String apiKey = "eca019664f9d3e107521d6295b1ef05a039b7a69";
        DefaultHttpClient httpClient = new DefaultHttpClient();            
        HttpResponse httpResponse = null;
        HttpEntity entity = null;
        HttpGet httpRequest = new HttpGet(url);       
        //httpRequest.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(restoConfiguration.getUser(), restoConfiguration.getPassword()), "UTF-8", false));
        httpRequest.setHeader("Content-Type","application/json");        
        ArrayList<Mesero> meseros = null;            
        try {
			httpResponse = httpClient.execute(httpRequest);
			entity = httpResponse.getEntity();
			String responseStr = EntityUtils.toString(entity);
			System.out.println("Response is: ["+responseStr+"]");
			if(responseStr==null||responseStr.trim().length()==0) {
				//La respuesta llegó vacia, posiblemente por un error de autenticación.
				meseros = new ArrayList<Mesero>();
			} else {
				meseros = parseMeseros(responseStr);
			}			
			return meseros;
		} catch (ClientProtocolException e) {				
			e.printStackTrace();
			return new ArrayList<Mesero>();
		} catch (IOException e) {				
			e.printStackTrace();				
			return new ArrayList<Mesero>();
		} catch (JSONException e) {
			e.printStackTrace();				
			return new ArrayList<Mesero>();
		}				
	}
	/**
	 * Given the JSON String convert it into a set of Meseros. 
	 * @param responseStr
	 * @return
	 * @throws JSONException
	 */
	private ArrayList<Mesero> parseMeseros(String responseStr) throws JSONException {
		ArrayList<Mesero> meseros = new ArrayList<Mesero>();
		JSONArray meserosJSON = new JSONArray(responseStr);
		for(int i = 0;i < meserosJSON.length();i++) {
			JSONObject meseroJSON = meserosJSON.getJSONObject(i);
			if(meseroJSON==null) {
				continue; 
			}	
			Mesero mesero = new Mesero();
			mesero.setId(meseroJSON.getString("_id"));
			mesero.setName(meseroJSON.getString("name"));
			ImageLoader imageLoader = ImageLoader.getInstance();
			mesero.setBitmap(imageLoader.loadImageSync(restoConfiguration.getUrl()+"/meseros/"+mesero.getId()+".jpg"));
			meseros.add(mesero);
		}
		return meseros;
	}
	@Override
	protected void onPostExecute(ArrayList<Mesero> result) {		
		super.onPostExecute(result);
		for (Mesero mesero : result) {
			mParent.get().addPictureInfo(mesero);
		}
		if(result.size()==0) {
			Toast.makeText(mParent.get(), R.string.meseros_error , Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(mParent.get(), R.string.meseros_done , Toast.LENGTH_SHORT).show();		
	}
	@Override
	protected void onPreExecute() {
		Toast.makeText(mParent.get(), R.string.meseros_load , Toast.LENGTH_SHORT).show();
	}
	
}
