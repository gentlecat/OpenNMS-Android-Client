package org.opennms.android;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import org.opennms.android.alarms.Alarm;
import org.opennms.android.alarms.AlarmsServerCommunication;
import org.opennms.android.alarms.AlarmsServerCommunicationImpl;
import org.opennms.android.alarms.dao.AlarmsListProvider;
import org.opennms.android.dao.DatabaseHelper;
import org.opennms.android.nodes.Node;
import org.opennms.android.nodes.NodesServerCommunication;
import org.opennms.android.nodes.NodesServerCommunicationImpl;
import org.opennms.android.nodes.dao.NodesListProvider;
import org.opennms.android.outages.Outage;
import org.opennms.android.outages.OutagesServerCommunication;
import org.opennms.android.outages.OutagesServerCommunicationImpl;
import org.opennms.android.outages.dao.OutagesListProvider;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainService extends Service {

    private static final String TAG = "MainService";
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
        Log.d(TAG, "Service started...");
        return START_STICKY;
    }

    public void refreshAlarms() {
        new Thread(new Runnable() {
            public void run() {
                Log.d(TAG, "Refreshing alarms...");
                try {
                    List<Alarm> alarms = alarmsServer.getAlarms("alarms");
                    for (Alarm alarm : alarms) {
                        ContentValues tutorialData = new ContentValues();
                        tutorialData.put(DatabaseHelper.COL_ALARM_ID, alarm.getId());
                        tutorialData.put(DatabaseHelper.COL_SEVERITY, alarm.getSeverity());
                        tutorialData.put(DatabaseHelper.COL_DESCRIPTION, alarm.getDescription());
                        tutorialData.put(DatabaseHelper.COL_LOG_MESSAGE, alarm.getLogMessage());
                        getContentResolver().insert(AlarmsListProvider.CONTENT_URI, tutorialData);
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
                        ContentValues tutorialData = new ContentValues();
                        tutorialData.put(DatabaseHelper.COL_NODE_ID, node.getId());
                        tutorialData.put(DatabaseHelper.COL_TYPE, node.getType());
                        tutorialData.put(DatabaseHelper.COL_LABEL, node.getLabel());
                        tutorialData.put(DatabaseHelper.COL_CREATED_TIME, node.getCreateTime());
                        tutorialData.put(DatabaseHelper.COL_SYS_CONTACT, node.getSysContact());
                        tutorialData.put(DatabaseHelper.COL_LABEL_SOURCE, node.getLabelSource());
                        getContentResolver().insert(NodesListProvider.CONTENT_URI, tutorialData);
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
                Log.d(TAG, "Huge success!");
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
                        ContentValues tutorialData = new ContentValues();
                        tutorialData.put(DatabaseHelper.COL_OUTAGE_ID, outage.getId());
                        tutorialData.put(DatabaseHelper.COL_IP_ADDRESS, outage.getIpAddress());
                        tutorialData.put(DatabaseHelper.COL_IF_REGAINED_SERVICE, outage.getIfRegainedService());
                        tutorialData.put(DatabaseHelper.COL_SERVICE_TYPE_NAME, outage.getServiceTypeName());
                        getContentResolver().insert(OutagesListProvider.CONTENT_URI, tutorialData);
                    }
                } catch (InterruptedException e) {
                    Log.i(TAG, e.getMessage());
                } catch (ExecutionException e) {
                    Log.i(TAG, e.getMessage());
                }
                Log.d(TAG, "Huge success!");
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed...");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service binded...");
        return binder;
    }

    public class LocalBinder extends Binder {
        public MainService getService() {
            // Return this instance of MainService so clients can call public methods
            return MainService.this;
        }
    }

}
