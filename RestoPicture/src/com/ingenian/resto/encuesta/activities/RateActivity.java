package com.ingenian.resto.encuesta.activities;

import java.util.Date;

import com.ingenian.resto.encuesta.R;
import com.ingenian.resto.encuesta.R.layout;
import com.ingenian.resto.encuesta.model.Encuesta;
import com.ingenian.resto.encuesta.model.Mesero;
import com.ingenian.resto.encuesta.model.RestoConfiguration;
import com.ingenian.resto.encuesta.tasks.EncuestaWebTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RateActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rate);
		Button sendButton = (Button) findViewById(R.id.rate_button);
		sendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Encuesta encuesta = new Encuesta();
				
				TextView textViewEMail = (TextView)findViewById(R.id.rate_email);
				if(textViewEMail.getText().length()>0) {
					encuesta.setEmail(textViewEMail.getText().toString());
				}
				TextView textViewComment = (TextView)findViewById(R.id.rate_comments);
				if(textViewComment.getText().length()>0) {
					encuesta.setComments(textViewComment.getText().toString());
				}
				Intent intent = getIntent();
				Mesero mesero =  (Mesero) intent.getExtras().getSerializable(EncuestaActivity.TAG);
				encuesta.setMesero(mesero);
				encuesta.setWhen(new Date());				
				RestoConfiguration restoConfiguration = (RestoConfiguration) intent.getExtras().getSerializable(EncuestaActivity.CONFIG);
				EncuestaWebTask encuestaWebTask = new EncuestaWebTask(RateActivity.this, restoConfiguration);
				encuestaWebTask.execute(encuesta);
			}
		});
	}
}
