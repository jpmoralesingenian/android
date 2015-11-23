package com.ingenian.resto.encuesta.model;

import java.io.Serializable;

import android.content.SharedPreferences;

/**
 * A simple object that contains the information needed for Redmine's parameters
 * @author Juan Pablo Morales
 *
 */
public class RestoConfiguration implements Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5430356482559064864L;
	private String url;
	
	public RestoConfiguration( String url) {
		super();
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * Creates the redmine configuration object from settings
	 */
	public static RestoConfiguration loadFromSettings(SharedPreferences sharedPref) {
		String restoURL = sharedPref.getString("pref_resto_url", "http://192.168.1.102:3000/");
		return new RestoConfiguration(restoURL);
	}
}
