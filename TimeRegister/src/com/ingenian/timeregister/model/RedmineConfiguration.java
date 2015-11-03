package com.ingenian.timeregister.model;

import android.content.SharedPreferences;

/**
 * A simple object that contains the information needed for Redmine's parameters
 * @author Juan Pablo Morales
 *
 */
public class RedmineConfiguration {
	private String user;
	private String password; 
	private String url;
	
	public RedmineConfiguration(String user, String password, String url) {
		super();
		this.user = user;
		this.password = password;
		this.url = url;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	public static RedmineConfiguration loadFromSettings(SharedPreferences sharedPref) {
		String redmineUser = sharedPref.getString("pref_redmine_user", null);
		String redminePassword = sharedPref.getString("pref_redmine_password", null);
		String redmineURL = sharedPref.getString("pref_redmine_url", "http://pqrs.ingenian.com/redmine/");
		return new RedmineConfiguration(redmineUser, redminePassword, redmineURL);
	}
	/**
	 * A configuration is considered empty if either user or password are null
	 * @return
	 */
	public boolean isEmpty() {
		System.err.println("In isEmpty: password:"+password+ " user: "+user+"->"+(password==null||user==null));
		return (password==null||user==null);
	}
}
