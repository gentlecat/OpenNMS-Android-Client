package org.opennms.android.dao.alarms;

import java.io.Serializable;

public class Alarm implements Serializable {

    private int id;
    private String severity;
    private String description;
    private String logMessage;

    public Alarm(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

}
