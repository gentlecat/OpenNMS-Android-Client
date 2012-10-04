package org.opennms.gsoc.alarms;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.opennms.gsoc.alarms.dao.AlarmsListProvider;
import org.opennms.gsoc.dao.OnmsDatabaseHelper;
import org.opennms.gsoc.model.OnmsAlarm;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AlarmsService extends Service {

    private static final String TAG = "AlarmsService";
    private AlarmsServerCommunication alarmsServer;

    @Override
    public void onCreate() {
        super.onCreate();
        this.alarmsServer = new AlarmsServerCommunicationImpl(getApplicationContext());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(AlarmsService.TAG, "Service started...");
        try {
            getAlarms();
        } catch (UnknownHostException e) {
            Log.i(AlarmsService.TAG, e.getMessage());
            getContentResolver().delete(AlarmsListProvider.CONTENT_URI, null, null);
        } catch (InterruptedException e) {
            Log.i(AlarmsService.TAG, e.getMessage());
            getContentResolver().delete(AlarmsListProvider.CONTENT_URI, null, null);
        } catch (ExecutionException e) {
            Log.i(AlarmsService.TAG, e.getMessage());
            getContentResolver().delete(AlarmsListProvider.CONTENT_URI, null, null);
        }catch (IOException e) {
            Log.i(AlarmsService.TAG, e.getMessage());
            getContentResolver().delete(AlarmsListProvider.CONTENT_URI, null, null);
        }

    }

    public void getAlarms() throws InterruptedException, ExecutionException, IOException {
        List<OnmsAlarm> alarms = this.alarmsServer.getAlarms("alarms");
        for(OnmsAlarm alarm : alarms) {
            ContentValues tutorialData = new ContentValues();
            tutorialData.put(OnmsDatabaseHelper.COL_ALARM_ID, alarm.getId());
            tutorialData.put(OnmsDatabaseHelper.COL_SEVERITY, alarm.getSeverity());
            tutorialData.put(OnmsDatabaseHelper.COL_DESCRIPTION, alarm.getDescription());
            tutorialData.put(OnmsDatabaseHelper.COL_LOG_MESSAGE, alarm.getLogMessage());
            getContentResolver().insert(AlarmsListProvider.CONTENT_URI, tutorialData);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(AlarmsService.TAG, "Service stopped...");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
