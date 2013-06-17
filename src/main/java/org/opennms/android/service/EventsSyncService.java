package org.opennms.android.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.opennms.android.communication.events.EventsServerCommunication;
import org.opennms.android.communication.events.EventsServerCommunicationImpl;
import org.opennms.android.dao.Columns;
import org.opennms.android.dao.events.Event;
import org.opennms.android.dao.events.EventsListProvider;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EventsSyncService extends IntentService {

    private static final String TAG = "EventsSyncService";

    public EventsSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ContentResolver contentResolver = getContentResolver();
        EventsServerCommunication eventsServer = new EventsServerCommunicationImpl(getApplicationContext());
        Log.d(TAG, "Synchronizing events...");
        try {
            List<Event> events = eventsServer.getEvents("events");
            for (Event event : events) insertEvent(contentResolver, event);
        } catch (UnknownHostException e) {
            Log.i(TAG, e.getMessage());
            contentResolver.delete(EventsListProvider.CONTENT_URI, null, null);
        } catch (InterruptedException e) {
            Log.i(TAG, e.getMessage());
            contentResolver.delete(EventsListProvider.CONTENT_URI, null, null);
        } catch (ExecutionException e) {
            Log.i(TAG, e.getMessage());
            contentResolver.delete(EventsListProvider.CONTENT_URI, null, null);
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            contentResolver.delete(EventsListProvider.CONTENT_URI, null, null);
        }
        Log.d(TAG, "Done!");
    }

    private Uri insertEvent(ContentResolver contentResolver, Event event) {
        ContentValues values = new ContentValues();
        values.put(Columns.EventColumns.COL_EVENT_ID, event.getId());
        values.put(Columns.EventColumns.COL_SEVERITY, event.getSeverity());
        values.put(Columns.EventColumns.COL_LOG_MESSAGE, event.getLogMessage());
        values.put(Columns.EventColumns.COL_DESCRIPTION, event.getDescription());
        values.put(Columns.EventColumns.COL_HOST, event.getHost());
        values.put(Columns.EventColumns.COL_IP_ADDRESS, event.getIpAddress());
        values.put(Columns.EventColumns.COL_CREATE_TIME, event.getCreateTime());
        values.put(Columns.EventColumns.COL_NODE_ID, event.getNodeId());
        values.put(Columns.EventColumns.COL_NODE_LABEL, event.getNodeLabel());
        return contentResolver.insert(EventsListProvider.CONTENT_URI, values);
    }

}
