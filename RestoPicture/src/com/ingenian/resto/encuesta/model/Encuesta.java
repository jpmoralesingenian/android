package com.ingenian.resto.encuesta.model;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Encuesta {
	private Mesero mesero;
	private Date when; 
	public Mesero getMesero() {
		return mesero;
	}
	public void setMesero(Mesero mesero) {
		this.mesero = mesero;
	}
	public Date getWhen() {
		return when;
	}
	public void setWhen(Date when) {
		this.when = when;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	private String comments;
	private String email;
	private int score;
	/**
	 * Return a JSON representation of this object
	 * @return
	 */
	public JSONObject toJSON() throws JSONException {
		JSONObject encuestaJSON = new JSONObject();		 
		encuestaJSON.put("score",score);
		if(mesero!=null) {
			JSONArray meserosJSON = new JSONArray();
			meserosJSON.put(mesero.toJSON());
			encuestaJSON.put("mesero",meserosJSON);
		}
		encuestaJSON.put("when",when);
		encuestaJSON.put("comments", comments);
		encuestaJSON.put("email",email);
		return encuestaJSON;
	}
	
}
