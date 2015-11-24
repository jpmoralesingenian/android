package com.ingenian.resto.encuesta.tasks;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.ingenian.resto.encuesta.R;
import com.ingenian.resto.encuesta.activities.EncuestaActivity;
import com.ingenian.resto.encuesta.model.Mesero;
import com.ingenian.resto.encuesta.storage.PhotoStore;
/**
 * This task is in charge of using a photostore to load all pictures into the graphic interface. 
 * Loading and processing bitmaps from disk can take a long time. 
 * The parameters are the file names (not the full URIs, only the name, no directory) to load.
 * 
 * @author JP Morales
 *
 */
public class FileLoaderTask extends AsyncTask<String, Void, ArrayList<Mesero>> {
	
	// Log TAG
	private static final String TAG = "FileLoaderTask";
	
	private WeakReference<EncuestaActivity> mParent;
	private PhotoStore photoStore;
	private int width;
	private int height;
	/**
	 * Create a new task that will use the given photoStore for file management.
	 * @param parent DailySelfieActivity that has the UI we will upgrade. 
	 * @param photoStore Class that knows how to handle. 
	 * @param width The width we will transform the pictures to.
	 * @param height The height we will transform the pictures to. 
	 */
	public FileLoaderTask(EncuestaActivity parent,PhotoStore photoStore, int width, int height) {
		super();

		mParent = new WeakReference<EncuestaActivity>(parent);
		this.photoStore = photoStore;
		this.width = width;
		this.height = height;
	}
	/**
	 * Given a String on names return a set of {@link Mesero} objects, using the {@link PhotoStore}
	 * @param params The list of filenames
	 * @return A PictureInfo for each filename with the bitmap adjusted to size. 
	 */
	@Override
	protected ArrayList<Mesero> doInBackground(String... params) {
		ArrayList<Mesero> pictureInfos = new ArrayList<Mesero>();
		
		for (String param:params) {
			pictureInfos.add(photoStore.getPictureInfo(param, width, height));
		}
		return pictureInfos;
	}
	/**
	 * With the set of pictureInfos we now have to put them into the ListView on the main Activity
	 */
	@Override
	protected void onPostExecute(ArrayList<Mesero> result) {
		for (Mesero pictureInfo : result) {
			mParent.get().addPictureInfo(pictureInfo);
		}			
	}
}
