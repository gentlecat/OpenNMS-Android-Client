package org.opennms.android.dao;

import java.io.Serializable;

public class Alarm implements Serializable {

    private int id;
    private String severity;
    private String description;
    private String logMessage;
    // Event
    private String firstEventTime;
    private String lastEventTime;
    private int lastEventId;
    private String lastEventSeverity;
    // Node
    private int nodeId;
    private String nodeLabel;
    // Service type
    private int serviceTypeId;
    private String serviceTypeName;

    public Alarm(int id) {
        this.id = id;
    }

    public int getId() {
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

    public String getFirstEventTime() {
        return firstEventTime;
    }

    public void setFirstEventTime(String firstEventTime) {
        this.firstEventTime = firstEventTime;
    }

    public String getLastEventTime() {
        return lastEventTime;
    }

    public void setLastEventTime(String lastEventTime) {
        this.lastEventTime = lastEventTime;
    }

    public int getLastEventId() {
        return lastEventId;
    }

    public void setLastEventId(int lastEventId) {
        this.lastEventId = lastEventId;
    }

    public String getLastEventSeverity() {
        return lastEventSeverity;
    }

    public void setLastEventSeverity(String lastEventSeverity) {
        this.lastEventSeverity = lastEventSeverity;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeLabel() {
        return nodeLabel;
    }

    public void setNodeLabel(String nodeLabel) {
        this.nodeLabel = nodeLabel;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

}
