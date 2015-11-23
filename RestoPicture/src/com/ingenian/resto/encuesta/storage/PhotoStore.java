package com.ingenian.resto.encuesta.storage;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



import com.ingenian.resto.encuesta.model.Mesero;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * A PhotoStore has all the facilities to retrieve from and to storage. 
 * This handles the naming, and retrieving of files. 
 * What we do is we store to a file using the Album name as a prefix. 
 * That way all files in the directory that start with the name will be a part of the collection. 
 * Part of this, specially the handling of Bitmaps was taken from http://developer.android.com/training/camera/photobasics.html* @author JP Morales
 *
 */
public class PhotoStore {	
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static String TAG="PhotoStore";
	/** This makes sure we check for the readiness of the storage and do nothing if it is not there */
	private boolean isReady = false;
	/** The name of the album */
	private String albumName;
	private File currentFile;
	private Activity parent;
	/** The actual directory where we store our stuff
	 */
	private File directory;
	/**
	 * Create a new photoStore for the given album.
	 * @param parent
	 * @param albumName
	 */
	public PhotoStore(Activity parent, String albumName) {
		this.albumName = albumName;	
		this.parent = parent;
	}	
	private String getFileName() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = albumName + "_" + JPEG_FILE_PREFIX + timeStamp + JPEG_FILE_SUFFIX;
		return imageFileName;
	}
	/**
	 * Given the name of a file, obtain the picture. 
	 * @param targetW The width you want the picture to have
	 * @param targetH The height you want the picture to have
	 * @param filename The name of the file (no directory)
	 * @return
	 */
	public Bitmap getBitmap(int targetW, int targetH, String filename) {

		if(!isReady) prepare();
		if(!isReady) {
			Log.e(TAG, "External storage is not usable");
		}
		File fCurrentPhotoPath = new File(directory,filename);
		String mCurrentPhotoPath = fCurrentPhotoPath.getAbsolutePath();
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		return bitmap;
		
	}
	/**
	 * Load the current file from Storage. This means a call to getFileForPicture must have been sent before. 
	 * @param width The width of the file we want returned on a {@link Mesero}
	 * @param height The height of the file we want returned on a {@link Mesero}
	 */
	public Mesero getPictureInfo(int width, int height) {
		if(currentFile==null) return null;
		String filename = currentFile.getName();		
		return getPictureInfo(filename, width,height);
	}
	/**
	 * Given a name, load it from storage and return the corresponding PictureInfo
	 */
	public Mesero getPictureInfo(String filename, int width, int height) {
		Bitmap bitmap = getBitmap(width, height, filename);
		Log.i(TAG,"The bitmap size is "+bitmap.getWidth()+","+bitmap.getHeight());
		Mesero pictureInfo = new Mesero(bitmap,filename);
		return pictureInfo;
	}
	/**
	 * Given a bitmap save it and return the object that can be shown. 
	 * @param mImageBitmap The bitmap to display
	 * @param mustSave true if we want to save, false if we don't. We don't want to save if we are using the larger picture
	 * @return
	 */
	public Mesero getPictureInfo(Bitmap mImageBitmap,boolean mustSave) {
		Mesero pictureInfo = new Mesero(mImageBitmap,getFileName() );
		if(mustSave) savePicture(pictureInfo);
		return pictureInfo;
	}
	/**
	 * Get the file for a new large picture and remember it so that it can be retrieved later 
	 * @return A reference to a file with the correct name to store a picture in.	
	 */
	public File getFileForPicture() {
		if(!isReady) prepare();
		if(!isReady) {
			Log.e(TAG,"Not ready when taking picture");
			return null;
		}
		currentFile =  new File(directory,getFileName());
		return currentFile;
	}
	/**
	 * Write the pictureInfo to external storage
	 * @param pictureInfo
	 * @return true if we could save, false otherwise. 
	 */
	private boolean savePicture(Mesero pictureInfo) {
		if(!isReady) prepare();
		if(!isReady) {
			Log.e(TAG,"The system was not ready. No external media");
			return false;
		}
		/** Now, let's do the heavy lifting */
		//YAY! http://stackoverflow.com/questions/649154/save-bitmap-to-location
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(directory,pictureInfo.getName()));
			pictureInfo.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, out);
			
		} catch(IOException e) {
			e.printStackTrace();
			Log.e(TAG,"Unable to save file "+pictureInfo.getName(),e);
			return false;
		} finally {
			try {
				if(out!=null) out.close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
				Log.e(TAG,"Unable to close file "+pictureInfo.getName(),ioe);
			}
		}
		return true;
	}
	/**
	 * Let us know if we are ready
	 * @return true if we can (and will) save pictures, false otherwise 
	 */
	private boolean prepare() {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Log.e(TAG,"External media not ready.");
			isReady = false;
			return false;
		}
		isReady = true;		
		directory = parent.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		return true;
	}
	/**
	 * Returns a list of all the pictures for the directory.
	 * Only JPG that start with the albumName will be returned here
	 * @return A list of filenames for pictures (no directory info there)
	 */
	public String[] getPictureNames() {
		if(!isReady) prepare();
		if(!isReady) {
			Log.e(TAG,"The system was not ready. No external media");
			return new String[]{};
		}
		File[] pictureFiles  = directory.listFiles(new AlbumFileFilter());
		String[] pictureFileNames = new String[pictureFiles.length];
		for(int a=0;a<pictureFiles.length;a++) {
			pictureFileNames[a] = pictureFiles[a].getName();
		}
		return pictureFileNames;
	}		
	/**
	 * This filter only accepts files that start with the album name and end in the suffix
	 * @author Juan Pablo Morales
	 *
	 */
	class AlbumFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			String name = pathname.getName();
			if(name==null) return false;
			return name.startsWith(albumName)&& name.endsWith(JPEG_FILE_SUFFIX);
		}
	}
}
