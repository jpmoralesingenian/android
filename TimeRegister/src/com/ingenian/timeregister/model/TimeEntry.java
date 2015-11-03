package com.ingenian.timeregister.model;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeEntry {
	private Project morningProject;
	private Project afternoonProject;
	private Date date;	
	
	public TimeEntry(Project morningProject, Project afternoonProject, Date date) {
		super();
		this.morningProject = morningProject;
		this.afternoonProject = afternoonProject;
		this.date = date;
	}

	/***
	 * Return the JSON representation of this TimeEntry.
	 * It can either be one or two entries.
	 * The structure of an entry is: 
	 * time_entry (required): a hash of the time entry attributes, including:
	 *   issue_id or project_id (only one is required): the issue id or project id to log time on
	 * 	 spent_on: the date the time was spent (default to the current date)
	 * 	 hours (required): the number of spent hours
	 * 	 activity_id: the id of the time activity. This parameter is required unless a default activity is defined in Redmine.
	 * 	 comments: short description for the entry (255 characters max)
	 */
	public String [] toJSON() {
		String[] answer;
		if(this.morningProject!=null&&this.afternoonProject!=null&&this.morningProject.getId()==this.afternoonProject.getId()) {
			//Same project
			answer = new String[1];
			answer[0] = toJSON(this.morningProject,8);
		} else {
			//Two projects
			answer = new String[2];
			answer[0] = toJSON(this.morningProject,4);
			answer[1] = toJSON(this.afternoonProject,4);
		}
		return answer;
	}
	/**
	 * Return the JSON for a single project entry. The date is given on the class wide property 
	 * @param project The project to create the entry for.
	 * @param hours The number of hours. 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private String toJSON(Project project, int hours) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer buffer = new StringBuffer();
		buffer.append("{ \"project_id\": ");
		buffer.append(project.getId());
		buffer.append(", ");
		buffer.append("\"spent_on\": \"");
		buffer.append(sdf.format(date));
		buffer.append("\",");
		buffer.append("\"hours\": ");
		buffer.append(hours);
		buffer.append(",");
		buffer.append("\"activity_id\": 18");
		buffer.append("}");
		return buffer.toString();
	}
}
