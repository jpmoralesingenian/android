package com.ingenian.resto.encuesta.activities;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ingenian.resto.encuesta.R;
import com.ingenian.resto.encuesta.model.Mesero;
import com.ingenian.resto.encuesta.model.PictureListAdapter;
import com.ingenian.resto.encuesta.model.RestoConfiguration;
import com.ingenian.resto.encuesta.storage.PhotoStore;
import com.ingenian.resto.encuesta.tasks.MeserosWebTask;



/**
 * Part of this was blatantly stolen from http://developer.android.com/training/camera/photobasics.html
 * 
 * This is the main activity. It is a simple ListView with an adapter, and delegates all storage functions 
 * to the {@link PhotoStore} class. 
 * 
 * This was tested on an Android emulator 18. Check each method for further detail on what is being done on 
 * each step.
 * 
 * @author Originally the guys on the page but, after extensive changes, Juan Ablo Morales
 *
 */
public class EncuestaActivity extends ListActivity {
	
	public static final String TAG = "EncuestaActivity";
	/** This is the key we use on the intent to store the information on the file name to open the larger picture */
	public static final String PICTURE_INFO = "Meseros";
	// The time to wait is two minutes
	public static final int SECONDS_TO_WAIT=2*60;
	public static final String CONFIG = "CONFIG";
	private PictureListAdapter mAdapter;
	private PhotoStore mPhotoStore;	
	private RestoConfiguration restoConfiguration;
	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}
	
	/** 
	 * Called when the activity is first created.
	 * This handles the following stats: 
	 * 1. Adapter setup, to use {@link PictureListAdapter} 
	 * 2. Event Listeners, so that clickng opens {@link LargeViewActivity}
	 * 3. Storage, so that other functions can use the storage engine
	 * 4. Storage loading, using an {@link AsyncTask} called {@link FileLoaderTask}
	 * 5. Alarm setup
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		mAdapter = new PictureListAdapter(getApplicationContext());
		setListAdapter(mAdapter);
		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Intent intent = new Intent(EncuestaActivity.this,RateActivity.class);
				intent.putExtra(TAG, (Mesero)mAdapter.getItem(position));
				intent.putExtra(CONFIG, restoConfiguration);
				Log.i(TAG,"Starting RateActivity");
				startActivity(intent);
				/*
				PictureInfo pictureInfo = (PictureInfo)mAdapter.getItem(position);
				Intent intent = new Intent(EncuestaActivity.this,LargeViewActivity.class);
				intent.putExtra(PICTURE_INFO, pictureInfo.getTitle());
				// Launch the Activity using the intent
				startActivity(intent);
				*/
				
			}
		});
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		restoConfiguration = RestoConfiguration.loadFromSettings(sharedPref);
		MeserosWebTask meserosWebTask =  new MeserosWebTask(this,restoConfiguration);
		meserosWebTask.execute();
		/*
		//Do storage things
		mPhotoStore = new PhotoStore(this,getAlbumName());
		FileLoaderTask fileLoaderTask = new FileLoaderTask(EncuestaActivity.this,mPhotoStore,80,60);
		fileLoaderTask.execute(mPhotoStore.getPictureNames());
		*/		
	}
	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	/**
	 * We don't have much to save, other than the pictures themselves
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
			packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	/**
	 * This method is invoked by the {@link FileLoaderTask} when it is ready to change the interface. 
	 * @param pictureInfo 
	 */
	public void addPictureInfo(Mesero pictureInfo) {
		mAdapter.add(pictureInfo);
	}
}