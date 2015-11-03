package com.ingenian.timeregister;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

public class DatePickerFragment extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {
	/**
	 * 
	 */
	private final TimeRegisterActivity timeRegisterActivity;
	/**
	 * @param timeRegisterActivity
	 */
	DatePickerFragment(TimeRegisterActivity timeRegisterActivity) {
		this.timeRegisterActivity = timeRegisterActivity;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		//Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(this.timeRegisterActivity, this, year, month, day);
	}
	public void onDateSet(DatePicker view, int year, int month, int day) {
		TextView txtFecha = (TextView)this.timeRegisterActivity.findViewById(R.id.date_label);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR,year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DATE,day);
		java.text.DateFormat sdf = SimpleDateFormat.getDateInstance();
		txtFecha.setText(sdf.format(c.getTime()));
	}
}