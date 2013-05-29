package org.opennms.android.dao.events;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import org.opennms.android.dao.Columns.EventColumns;

import java.io.Serializable;

public class Event implements Serializable {

    private int id;
    private String severity;
    private String logMessage;
    private String description;
    private String host;
    private String ipAddress;
    private int nodeId;
    private String nodeLabel;

    public Event(Integer id) {
        this.id = id;
    }

    public Event(ContentResolver contentResolver, long listItemId) {
        String projection[] = {
                EventColumns.COL_EVENT_ID,
                EventColumns.COL_SEVERITY,
                EventColumns.COL_DESCRIPTION
        };
        Cursor eventsCursor = contentResolver.query(
                Uri.withAppendedPath(EventsListProvider.CONTENT_URI, String.valueOf(listItemId)),
                projection, null, null, null);
        if (eventsCursor.moveToFirst()) {
            id = eventsCursor.getInt(0);
            severity = eventsCursor.getString(1);
            description = eventsCursor.getString(2);
        }
        eventsCursor.close();
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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

}
