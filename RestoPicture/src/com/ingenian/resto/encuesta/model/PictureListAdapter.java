package com.ingenian.resto.encuesta.model;

import java.util.ArrayList;

import com.ingenian.resto.encuesta.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * This adapter handles a list of {@link Mesero}.
 * The code is adapted from Lab8's example of an adapter, modified to hold pictures. 
 * @author JP Morales
 *
 */
public class PictureListAdapter extends BaseAdapter {

	private ArrayList<Mesero> list = new ArrayList<Mesero>();
	private static LayoutInflater inflater = null;
	private Context mContext;

	public PictureListAdapter(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	/**
	 * Inflates the single_picture layout 
	 */
	public View getView(int position, View convertView, ViewGroup parent) {

		View newView = convertView;
		ViewHolder holder;

		Mesero curr = list.get(position);

		if (null == convertView) {
			holder = new ViewHolder();
			newView = inflater
					.inflate(R.layout.single_picture, parent, false);
			holder.picture = (ImageView) newView.findViewById(R.id.sp_picture);
			holder.filename = (TextView) newView.findViewById(R.id.sp_filename);			
			newView.setTag(holder);

		} else {
			holder = (ViewHolder) newView.getTag();
		}

		holder.picture.setImageBitmap(curr.getBitmap());
		holder.filename.setText(curr.getName());
		

		return newView;
	}

	static class ViewHolder {

		ImageView picture;
		TextView filename;		

	}	

	public void add(Mesero listItem) {
		list.add(listItem);
		notifyDataSetChanged();
	}

	public ArrayList<Mesero> getList() {
		return list;
	}

	public void removeAllViews() {
		list.clear();
		this.notifyDataSetChanged();
	}
}
