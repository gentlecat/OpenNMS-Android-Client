package org.opennms.android.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.opennms.android.App;
import org.opennms.android.data.api.model.Alarm;
import org.opennms.android.data.api.model.Event;
import org.opennms.android.data.api.model.Node;
import org.opennms.android.data.api.model.Outage;

import java.util.List;

import javax.inject.Inject;

public class UpdateManager extends Service {
    private static final String TAG = "UpdateManager";

    @Inject
    Updater updater;

    @Override
    public void onCreate() {
        App.get(this).inject(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        /** We want this service to continue running until
         * it is explicitly stopped, so return sticky. */
        return START_STICKY;
    }

    // Object that receives interactions from clients
    private final IBinder binder = new LocalBinder();

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public UpdateManager getService() {
            return UpdateManager.this;
        }
    }

    public enum LoadType {NODES, ALARMS, EVENTS, OUTAGES}

    boolean nodesLoadActive = false;
    boolean alarmsLoadActive = false;
    boolean eventsLoadActive = false;
    boolean outagesLoadActive = false;

    public boolean isLoading(LoadType loadType) {
        switch (loadType) {
            case NODES:
                return nodesLoadActive;
            case ALARMS:
                return alarmsLoadActive;
            case EVENTS:
                return eventsLoadActive;
            case OUTAGES:
                return outagesLoadActive;
        }
        return false;
    }

    public void startLoading(LoadType loadType, int limit, int offset) {
        if (isLoading(loadType)) {
            Log.i(TAG, "Already loading.");
            return;
        }
        switch (loadType) {
            case NODES:
                new NodesLoader(limit, offset).execute();
                break;
            case ALARMS:
                new AlarmsLoader(limit, offset).execute();
                break;
            case EVENTS:
                new EventsLoader(limit, offset).execute();
                break;
            case OUTAGES:
                new OutagesLoader(limit, offset).execute();
                break;
        }
    }

    class NodesLoader extends AsyncTask<Void, Void, Boolean> {
        private int limit, offset;

        public NodesLoader(int limit, int offset) {
            this.limit = limit;
            this.offset = offset;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nodesLoadActive = true;
        }

        protected Boolean doInBackground(Void... voids) {
            return updater.updateNodes(limit, offset);
        }

        protected void onPostExecute(List<Node> nodes) {
            nodesLoadActive = false;
        }
    }

    class AlarmsLoader extends AsyncTask<Void, Void, Boolean> {
        private int limit, offset;

        public AlarmsLoader(int limit, int offset) {
            this.limit = limit;
            this.offset = offset;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alarmsLoadActive = true;
        }

        protected Boolean doInBackground(Void... voids) {
            return updater.updateAlarms(limit, offset);
        }

        protected void onPostExecute(List<Alarm> alarms) {
            alarmsLoadActive = false;
        }
    }

    class EventsLoader extends AsyncTask<Void, Void, Boolean> {
        private int limit, offset;

        public EventsLoader(int limit, int offset) {
            this.limit = limit;
            this.offset = offset;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            eventsLoadActive = true;
        }

        protected Boolean doInBackground(Void... voids) {
            return updater.updateEvents(limit, offset);
        }

        protected void onPostExecute(List<Event> events) {
            eventsLoadActive = false;
        }
    }

    class OutagesLoader extends AsyncTask<Void, Void, Boolean> {
        private int limit, offset;

        public OutagesLoader(int limit, int offset) {
            this.limit = limit;
            this.offset = offset;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            outagesLoadActive = true;
        }

        protected Boolean doInBackground(Void... voids) {
            return updater.updateOutages(limit, offset);
        }

        protected void onPostExecute(List<Outage> outages) {
            outagesLoadActive = false;
        }
    }

}