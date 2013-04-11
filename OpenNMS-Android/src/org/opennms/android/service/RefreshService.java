package org.opennms.android.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import org.opennms.android.communication.alarms.AlarmsServerCommunication;
import org.opennms.android.communication.alarms.AlarmsServerCommunicationImpl;
import org.opennms.android.communication.nodes.NodesServerCommunication;
import org.opennms.android.communication.nodes.NodesServerCommunicationImpl;
import org.opennms.android.communication.outages.OutagesServerCommunication;
import org.opennms.android.communication.outages.OutagesServerCommunicationImpl;
import org.opennms.android.dao.Columns;
import org.opennms.android.dao.alarms.Alarm;
import org.opennms.android.dao.alarms.AlarmsListProvider;
import org.opennms.android.dao.nodes.Node;
import org.opennms.android.dao.nodes.NodesListProvider;
import org.opennms.android.dao.outages.Outage;
import org.opennms.android.dao.outages.OutagesListProvider;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RefreshService extends Service {

    private static final String TAG = "RefreshService";
    private final IBinder binder = new LocalBinder();
    private AlarmsServerCommunication alarmsServer;
    private NodesServerCommunication nodesServer;
    private OutagesServerCommunication outagesServer;

    @Override
    public void onCreate() {
        super.onCreate();
        alarmsServer = new AlarmsServerCommunicationImpl(getApplicationContext());
        nodesServer = new NodesServerCommunicationImpl(getApplicationContext());
        outagesServer = new OutagesServerCommunicationImpl(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "RefreshService started...");
        return START_STICKY;
    }

    public void refreshAlarms() {
        new Thread(new Runnable() {
            public void run() {
                Log.d(TAG, "Refreshing alarms...");
                try {
                    List<Alarm> alarms = alarmsServer.getAlarms("alarms");
                    for (Alarm alarm : alarms) {
                        ContentValues values = new ContentValues();
                        values.put(Columns.AlarmColumns.COL_ALARM_ID, alarm.getId());
                        values.put(Columns.AlarmColumns.COL_SEVERITY, alarm.getSeverity());
                        values.put(Columns.AlarmColumns.COL_DESCRIPTION, alarm.getDescription());
                        values.put(Columns.AlarmColumns.COL_LOG_MESSAGE, alarm.getLogMessage());
                        getContentResolver().insert(AlarmsListProvider.CONTENT_URI, values);
                    }
                } catch (UnknownHostException e) {
                    Log.i(TAG, e.getMessage());
                    getContentResolver().delete(AlarmsListProvider.CONTENT_URI, null, null);
                } catch (InterruptedException e) {
                    Log.i(TAG, e.getMessage());
                    getContentResolver().delete(AlarmsListProvider.CONTENT_URI, null, null);
                } catch (ExecutionException e) {
                    Log.i(TAG, e.getMessage());
                    getContentResolver().delete(AlarmsListProvider.CONTENT_URI, null, null);
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                    getContentResolver().delete(AlarmsListProvider.CONTENT_URI, null, null);
                }
                Log.d(TAG, "Alarms refreshing completed.");
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
                        getContentResolver().insert(NodesListProvider.CONTENT_URI, values);
                    }
                } catch (UnknownHostException e) {
                    Log.i(TAG, e.getMessage());
                    getContentResolver().delete(NodesListProvider.CONTENT_URI, null, null);
                } catch (InterruptedException e) {
                    Log.i(TAG, e.getMessage());
                    getContentResolver().delete(NodesListProvider.CONTENT_URI, null, null);
                } catch (ExecutionException e) {
                    Log.i(TAG, e.getMessage());
                    getContentResolver().delete(NodesListProvider.CONTENT_URI, null, null);
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                    getContentResolver().delete(NodesListProvider.CONTENT_URI, null, null);
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
                        values.put(Columns.OutageColumns.COL_SERVICE_TYPE_NAME, outage.getServiceTypeName());
                        getContentResolver().insert(OutagesListProvider.CONTENT_URI, values);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "RefreshService destroyed.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "RefreshService binded.");
        return binder;
    }

    public class LocalBinder extends Binder {
        public RefreshService getService() {
            // Return this instance of RefreshService so clients can call public methods
            return RefreshService.this;
        }
    }

}
