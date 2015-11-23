package com.ingenian.resto.encuesta.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/**
 * This is a model class that holds one photo 
 * @author JP Morales
 *
 */
public class Mesero implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4063962938143742836L;
	private transient Bitmap bitmap;
	private String name;
	private String id;
	public Mesero() {
	
	}
	public Mesero(Bitmap bitmap, String title) {
		this.bitmap= bitmap;
		this.name = title;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public String getName() {
		return name;
	}
	public void setName(String title) {
		this.name = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * Crea el objeto de JSON correspondiente a este mesero, que tiene solo id y nombre
	 * @return
	 */
	public JSONObject toJSON() throws JSONException {
		JSONObject meseroJSON = new JSONObject();
		meseroJSON.put("_id",id);
		meseroJSON.put("name",name);
		return meseroJSON;
	}	
}
