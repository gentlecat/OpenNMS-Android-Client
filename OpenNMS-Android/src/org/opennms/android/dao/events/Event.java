package org.opennms.android.dao.events;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import org.opennms.android.dao.Columns.EventColumns;

import java.io.Serializable;

public class Event implements Serializable {

    private int id;
    private String severity;
    private String description;

    public Event(Integer id, String severity, String description) {
        this.id = id;
        this.severity = severity;
        this.description = description;
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

    public Integer getId() {
        return id;
    }

    public String getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }

}
