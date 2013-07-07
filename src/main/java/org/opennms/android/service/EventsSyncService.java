package org.opennms.android.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.opennms.android.communication.events.EventsServerCommunication;
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
        EventsServerCommunication eventsServer = new EventsServerCommunication(getApplicationContext());
        Log.i(TAG, "Synchronizing events...");
        try {
            List<Event> events = eventsServer.getEvents("events?orderBy=id&order=desc");
            contentResolver.delete(EventsListProvider.CONTENT_URI, null, null);
            for (Event event : events) insertEvent(contentResolver, event);
            Log.i(TAG, "Done!");
        } catch (UnknownHostException e) {
            Log.e(TAG, "UnknownHostException", e);
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException", e);
        } catch (ExecutionException e) {
            Log.e(TAG, "ExecutionException", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }
    }

    private Uri insertEvent(ContentResolver contentResolver, Event event) {
        ContentValues values = new ContentValues();
        values.put(Columns.EventColumns.EVENT_ID, event.getId());
        values.put(Columns.EventColumns.SEVERITY, event.getSeverity());
        values.put(Columns.EventColumns.LOG_MESSAGE, event.getLogMessage());
        values.put(Columns.EventColumns.DESCRIPTION, event.getDescription());
        values.put(Columns.EventColumns.HOST, event.getHost());
        values.put(Columns.EventColumns.IP_ADDRESS, event.getIpAddress());
        values.put(Columns.EventColumns.CREATE_TIME, event.getCreateTime());
        values.put(Columns.EventColumns.NODE_ID, event.getNodeId());
        values.put(Columns.EventColumns.NODE_LABEL, event.getNodeLabel());
        values.put(Columns.EventColumns.SERVICE_TYPE_ID, event.getServiceTypeId());
        values.put(Columns.EventColumns.SERVICE_TYPE_NAME, event.getServiceTypeName());
        return contentResolver.insert(EventsListProvider.CONTENT_URI, values);
    }

}
