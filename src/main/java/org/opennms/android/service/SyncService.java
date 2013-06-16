package org.opennms.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import org.opennms.android.R;
import org.opennms.android.communication.alarms.AlarmsServerCommunication;
import org.opennms.android.communication.alarms.AlarmsServerCommunicationImpl;
import org.opennms.android.communication.events.EventsServerCommunication;
import org.opennms.android.communication.events.EventsServerCommunicationImpl;
import org.opennms.android.communication.nodes.NodesServerCommunication;
import org.opennms.android.communication.nodes.NodesServerCommunicationImpl;
import org.opennms.android.communication.outages.OutagesServerCommunication;
import org.opennms.android.communication.outages.OutagesServerCommunicationImpl;
import org.opennms.android.dao.Columns;
import org.opennms.android.dao.alarms.Alarm;
import org.opennms.android.dao.alarms.AlarmsListProvider;
import org.opennms.android.dao.events.Event;
import org.opennms.android.dao.events.EventsListProvider;
import org.opennms.android.dao.nodes.Node;
import org.opennms.android.dao.nodes.NodesListProvider;
import org.opennms.android.dao.outages.Outage;
import org.opennms.android.dao.outages.OutagesListProvider;
import org.opennms.android.ui.MainActivity;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SyncService extends Service {

    private static final String TAG = "SyncService";
    private static final int ALARM_NOTIFICATION_ID = 1;
    private final IBinder binder = new LocalBinder();
    private AlarmsServerCommunication alarmsServer;
    private EventsServerCommunication eventsServer;
    private NodesServerCommunication nodesServer;
    private OutagesServerCommunication outagesServer;
    private ContentResolver contentResolver;
    private SharedPreferences sharedPref;

    @Override
    public void onCreate() {
        super.onCreate();

        alarmsServer = new AlarmsServerCommunicationImpl(getApplicationContext());
        eventsServer = new EventsServerCommunicationImpl(getApplicationContext());
        nodesServer = new NodesServerCommunicationImpl(getApplicationContext());
        outagesServer = new OutagesServerCommunicationImpl(getApplicationContext());

        contentResolver = getContentResolver();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "SyncService started...");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SyncService is destroyed.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "SyncService is bound.");
        return binder;
    }

    public void refreshAlarms() {
        new Thread(new Runnable() {
            public void run() {
                int latestShownAlarmId = sharedPref.getInt("latest_shown_alarm_id", 0);
                int newAlarmsCount = 0, maxId = 0;

                Log.d(TAG, "Refreshing alarms...");
                try {
                    List<Alarm> alarms = alarmsServer.getAlarms("alarms");
                    for (Alarm alarm : alarms) {
                        ContentValues values = new ContentValues();
                        values.put(Columns.AlarmColumns.COL_ALARM_ID, alarm.getId());
                        values.put(Columns.AlarmColumns.COL_SEVERITY, alarm.getSeverity());
                        values.put(Columns.AlarmColumns.COL_DESCRIPTION, alarm.getDescription());
                        values.put(Columns.AlarmColumns.COL_LOG_MESSAGE, alarm.getLogMessage());
                        contentResolver.insert(AlarmsListProvider.CONTENT_URI, values);

                        if (alarm.getId() > latestShownAlarmId) newAlarmsCount++;
                        if (alarm.getId() > maxId) maxId = alarm.getId();
                    }
                } catch (UnknownHostException e) {
                    Log.i(TAG, e.getMessage());
                    contentResolver.delete(AlarmsListProvider.CONTENT_URI, null, null);
                } catch (InterruptedException e) {
                    Log.i(TAG, e.getMessage());
                    contentResolver.delete(AlarmsListProvider.CONTENT_URI, null, null);
                } catch (ExecutionException e) {
                    Log.i(TAG, e.getMessage());
                    contentResolver.delete(AlarmsListProvider.CONTENT_URI, null, null);
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                    contentResolver.delete(AlarmsListProvider.CONTENT_URI, null, null);
                }
                Log.d(TAG, "Alarms refreshing completed.");

                sharedPref.edit().putInt("latest_shown_alarm_id", maxId).commit();
                if (newAlarmsCount > 0) issueNewAlarmsNotification(newAlarmsCount);
            }
        }).start();
    }

    public void refreshEvents() {
        new Thread(new Runnable() {
            public void run() {
                Log.d(TAG, "Refreshing events...");
                try {
                    List<Event> events = eventsServer.getEvents("events");
                    for (Event event : events) {
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
                        contentResolver.insert(EventsListProvider.CONTENT_URI, values);
                    }
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
                Log.d(TAG, "Events refreshing completed.");
            }
        }).start();
    }

    public void refreshNodes() {
        new Thread(new Runnable() {
            public void run() {
                Log.d(TAG, "Refreshing nodes...");
                try {
                    List<Node> nodes = nodesServer.getNodes("nodes");
                    for (Node node : nodes) {
                        ContentValues values = new ContentValues();
                        values.put(Columns.NodeColumns.COL_NODE_ID, node.getId());
                        values.put(Columns.NodeColumns.COL_TYPE, node.getType());
                        values.put(Columns.NodeColumns.COL_LABEL, node.getLabel());
                        values.put(Columns.NodeColumns.COL_CREATED_TIME, node.getCreateTime());
                        values.put(Columns.NodeColumns.COL_SYS_CONTACT, node.getSysContact());
                        values.put(Columns.NodeColumns.COL_LABEL_SOURCE, node.getLabelSource());
                        contentResolver.insert(NodesListProvider.CONTENT_URI, values);
                    }
                } catch (UnknownHostException e) {
                    Log.i(TAG, e.getMessage());
                    contentResolver.delete(NodesListProvider.CONTENT_URI, null, null);
                } catch (InterruptedException e) {
                    Log.i(TAG, e.getMessage());
                    contentResolver.delete(NodesListProvider.CONTENT_URI, null, null);
                } catch (ExecutionException e) {
                    Log.i(TAG, e.getMessage());
                    contentResolver.delete(NodesListProvider.CONTENT_URI, null, null);
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                    contentResolver.delete(NodesListProvider.CONTENT_URI, null, null);
                }
                Log.d(TAG, "Node refreshing completed.");
            }
        }).start();
    }

    public void refreshOutages() {
        new Thread(new Runnable() {
            public void run() {
                Log.d(TAG, "Refreshing outages...");
                try {
                    List<Outage> outages = outagesServer.getOutages("outages");
                    for (Outage outage : outages) {
                        ContentValues values = new ContentValues();
                        values.put(Columns.OutageColumns.COL_OUTAGE_ID, outage.getId());
                        values.put(Columns.OutageColumns.COL_IP_ADDRESS, outage.getIpAddress());
                        values.put(Columns.OutageColumns.COL_IF_REGAINED_SERVICE, outage.getIfRegainedService());
                        values.put(Columns.OutageColumns.COL_IF_LOST_SERVICE, outage.getIfRegainedService());
                        values.put(Columns.OutageColumns.COL_SERVICE_TYPE_NAME, outage.getServiceTypeName());
                        contentResolver.insert(OutagesListProvider.CONTENT_URI, values);
                    }
                } catch (InterruptedException e) {
                    Log.i(TAG, e.getMessage());
                } catch (ExecutionException e) {
                    Log.i(TAG, e.getMessage());
                }
                Log.d(TAG, "Outages refreshing completed.");
            }
        }).start();
    }

    private void issueNewAlarmsNotification(int newAlarmsCount) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Constructs the Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle(getString(R.string.alarms_notification_title))
                .setContentText(String.format(getString(R.string.alarms_notification_text), newAlarmsCount))
                .setDefaults(Notification.DEFAULT_ALL); // requires VIBRATE permission

        // Clicking the notification itself displays MainActivity.
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        /*
         * Because clicking the notification opens a new ("special") activity,
         * there's no need to create an artificial back stack.
         */
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(ALARM_NOTIFICATION_ID, builder.build());
    }

    public class LocalBinder extends Binder {
        public SyncService getService() {
            // Return this instance of SyncService so clients can call public methods
            return SyncService.this;
        }
    }

}
