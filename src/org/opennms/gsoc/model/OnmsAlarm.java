package org.opennms.gsoc.model;

import java.io.Serializable;

public class OnmsAlarm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String severity;
	private String description;
	private String logMessage;

	public OnmsAlarm(Integer id, String severity, String description, String logMessage) {
		this.id = id;
		this.severity = severity;
		this.description = description;
		this.logMessage = logMessage;
	}

	public Integer getId() {
		return this.id;
	}
	public String getSeverity() {
		return this.severity;
	}
	public String getDescription() {
		return this.description;
	}
	public String getLogMessage() {
		return this.logMessage;
	}

	@Override
	public String toString() {
		return "OnmsAlarm [id=" + this.id + ", severity=" + this.severity
				+ ", description=" + this.description + ", logMessage=" + this.logMessage
				+ "]";
	}


}
