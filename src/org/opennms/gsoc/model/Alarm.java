package org.opennms.gsoc.model;

import java.io.Serializable;

public class Alarm implements Serializable {

    private Integer id;
    private String severity;
    private String description;
    private String logMessage;

    public Alarm(Integer id, String severity, String description, String logMessage) {
        this.id = id;
        this.severity = severity;
        this.description = description;
        this.logMessage = logMessage;
    }

    public Integer getId() {
        return id;
    }

    public String getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }

    public String getLogMessage() {
        return logMessage;
    }

    @Override
    public String toString() {
        return "Alarm [id=" + id + ", severity=" + severity + ", description=" + description + ", logMessage=" + logMessage + "]";
    }

}
