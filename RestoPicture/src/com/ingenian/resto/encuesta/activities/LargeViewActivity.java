package com.ingenian.resto.encuesta.activities;

import com.ingenian.resto.encuesta.R;
import com.ingenian.resto.encuesta.model.Mesero;
import com.ingenian.resto.encuesta.storage.PhotoStore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * This is the activity that shows the large image. 
 * The interface is just an ImageView and a TextView. 
 * Ii receives the filename to load via Intent and uses a PhotoStore to load it. 
 * @author JP Morales
 *
 */
public class LargeViewActivity extends Activity {
	private PhotoStore mPhotoStore;
	/**
	 * Check for the intent, if not present put a message. If present load from disk using {@link PhotoStore}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_large_view);
		Intent intent = getIntent();
		String filename =  intent.getExtras().getString(EncuestaActivity.PICTURE_INFO);
		TextView titleView = (TextView) findViewById(R.id.lv_text);
		ImageView imageView = (ImageView) findViewById(R.id.lv_image);
		mPhotoStore = new PhotoStore(this, getString(R.string.album_name));
		if(filename==null) {
			titleView.setText(R.string.msg_nothing);
		} else {
			Mesero pictureInfo = mPhotoStore.getPictureInfo(filename,imageView.getWidth(),imageView.getHeight());
			titleView.setText(pictureInfo.getName());
			imageView.setImageBitmap(pictureInfo.getBitmap());
		}		
	}
}
