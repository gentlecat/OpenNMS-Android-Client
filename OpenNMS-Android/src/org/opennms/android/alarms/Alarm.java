package org.opennms.android.alarms;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import org.opennms.android.alarms.dao.AlarmsListProvider;
import org.opennms.android.dao.DatabaseHelper;

import java.io.Serializable;

public class Alarm implements Serializable {

    private int id;
    private String severity;
    private String description;
    private String logMessage;

    public Alarm(Integer id, String severity, String description, String logMessage) {
        this.id = id;
        this.severity = severity;
        this.description = description;
        this.logMessage = logMessage;
    }

    public Alarm(ContentResolver contentResolver, long listItemId) {
        String projection[] = {
                DatabaseHelper.COL_ALARM_ID,
                DatabaseHelper.COL_SEVERITY,
                DatabaseHelper.COL_DESCRIPTION,
                DatabaseHelper.COL_LOG_MESSAGE
        };
        Cursor alarmsCursor = contentResolver.query(
                Uri.withAppendedPath(AlarmsListProvider.CONTENT_URI, String.valueOf(listItemId)),
                projection, null, null, null);
        if (alarmsCursor.moveToFirst()) {
            id = alarmsCursor.getInt(0);
            severity = alarmsCursor.getString(1);
            description = alarmsCursor.getString(2);
            logMessage = alarmsCursor.getString(3);
        }
        alarmsCursor.close();
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
