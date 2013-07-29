package org.opennms.android.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.parsing.AlarmsParser;
import org.opennms.android.parsing.EventsParser;
import org.opennms.android.parsing.NodesParser;
import org.opennms.android.parsing.OutagesParser;
import org.opennms.android.provider.Contract;

import java.util.ArrayList;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";
    private ContentResolver contentResolver;
    private ServerCommunication serverCommunication;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
        serverCommunication = new ServerCommunication(context);
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        contentResolver = context.getContentResolver();
        serverCommunication = new ServerCommunication(context);
    }

    public static final int SYNC_TYPE_NODES = 1;
    public static final int SYNC_TYPE_EVENTS = 2;
    public static final int SYNC_TYPE_ALARMS = 3;
    public static final int SYNC_TYPE_OUTAGES = 4;
    public static final String SYNC_TYPE_EXTRA_KEY = "sync_type";

    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {
        int syncType = extras.getInt(SYNC_TYPE_EXTRA_KEY);
        switch (syncType) {
            case SYNC_TYPE_NODES:
                syncNodes();
                break;
            case SYNC_TYPE_EVENTS:
                syncEvents();
                break;
            case SYNC_TYPE_ALARMS:
                syncAlarms();
                break;
            case SYNC_TYPE_OUTAGES:
                syncOutages();
                break;
            default:
                Log.wtf(TAG, "Wrong sync type extra value!");
                break;
        }
    }

    private void syncNodes() {
        String result;
        try {
            result = serverCommunication.get("nodes/?limit=0");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during synchronization process", e);
            return;
        }
        contentResolver.delete(Contract.Nodes.CONTENT_URI, null, null);
        ArrayList<ContentValues> values = NodesParser.parse(result);
        contentResolver.bulkInsert(Contract.Nodes.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
    }

    private void syncEvents() {
        String result;
        try {
            result = serverCommunication.get("events?orderBy=id&order=desc&limit=25");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during synchronization process", e);
            return;
        }
        contentResolver.delete(Contract.Events.CONTENT_URI, null, null);
        ArrayList<ContentValues> values = EventsParser.parse(result);
        contentResolver.bulkInsert(Contract.Events.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
    }

    private void syncAlarms() {
        String result;
        try {
            result = serverCommunication.get("alarms?orderBy=id&order=desc&limit=0");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during synchronization process", e);
            return;
        }
        contentResolver.delete(Contract.Alarms.CONTENT_URI, null, null);
        ArrayList<ContentValues> values = AlarmsParser.parse(result);
        contentResolver.bulkInsert(Contract.Alarms.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
    }

    private void syncOutages() {
        String result;
        try {
            result = serverCommunication.get("outages?orderBy=id&order=desc&limit=25");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during synchronization process", e);
            return;
        }
        contentResolver.delete(Contract.Outages.CONTENT_URI, null, null);
        ArrayList<ContentValues> values = OutagesParser.parse(result);
        contentResolver.bulkInsert(Contract.Outages.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
    }

}