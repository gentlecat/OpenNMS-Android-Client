package org.opennms.android.sync;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.opennms.android.App;
import org.opennms.android.data.api.ServerInterface;
import org.opennms.android.data.api.model.Alarm;
import org.opennms.android.data.api.model.Event;
import org.opennms.android.data.api.model.Node;
import org.opennms.android.data.api.model.Outage;
import org.opennms.android.data.ContentValuesGenerator;
import org.opennms.android.data.storage.Contract;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LoadManager extends Service {

    private static final String TAG = "LoadManager";
    @Inject
    ServerInterface server;

    @Override
    public void onCreate() {
        App app = App.get(this);
        app.inject(this);
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
        public LoadManager getService() {
            return LoadManager.this;
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

    class NodesLoader extends AsyncTask<Void, Void, List<Node>> {
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

        protected List<Node> doInBackground(Void... voids) {
            List<Node> nodes;
            try {
                nodes = server.nodes(limit, offset).nodes;
            } catch (Exception e) {
                Log.e(TAG, "Error occurred during nodes loading!", e);
                return null;
            }

            /** Updating database records */
            ArrayList<ContentValues> values = ContentValuesGenerator.fromNodes(nodes);
            getContentResolver().bulkInsert(Contract.Nodes.CONTENT_URI,
                    values.toArray(new ContentValues[values.size()]));

            return nodes;
        }

        protected void onPostExecute(List<Node> nodes) {
            nodesLoadActive = false;
        }
    }

    class AlarmsLoader extends AsyncTask<Void, Void, List<Alarm>> {
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

        protected List<Alarm> doInBackground(Void... voids) {
            List<Alarm> alarms;
            try {
                alarms = server.alarms(limit, offset).alarms;
            } catch (Exception e) {
                Log.e(TAG, "Error occurred during alarms loading!", e);
                return null;
            }

            /** Updating database records */
            ArrayList<ContentValues> values = ContentValuesGenerator.fromAlarms(alarms);
            getContentResolver().bulkInsert(Contract.Alarms.CONTENT_URI,
                    values.toArray(new ContentValues[values.size()]));

            return alarms;
        }

        protected void onPostExecute(List<Alarm> alarms) {
            alarmsLoadActive = false;
        }
    }

    class EventsLoader extends AsyncTask<Void, Void, List<Event>> {
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

        protected List<Event> doInBackground(Void... voids) {
            List<Event> events;
            try {
                events = server.events(limit, offset).events;
            } catch (Exception e) {
                Log.e(TAG, "Error occurred during events loading!", e);
                return null;
            }

            /** Updating database records */
            ArrayList<ContentValues> values = ContentValuesGenerator.fromEvents(events);
            getContentResolver().bulkInsert(Contract.Events.CONTENT_URI,
                    values.toArray(new ContentValues[values.size()]));

            return events;
        }

        protected void onPostExecute(List<Event> events) {
            eventsLoadActive = false;
        }
    }

    class OutagesLoader extends AsyncTask<Void, Void, List<Outage>> {
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

        protected List<Outage> doInBackground(Void... voids) {
            List<Outage> outages;
            try {
                outages = server.outages(limit, offset).outages;
            } catch (Exception e) {
                Log.e(TAG, "Error occurred during outages loading!", e);
                return null;
            }

            /** Updating database records */
            ArrayList<ContentValues> values = ContentValuesGenerator.fromOutages(outages);
            getContentResolver().bulkInsert(Contract.Outages.CONTENT_URI,
                    values.toArray(new ContentValues[values.size()]));

            return outages;
        }

        protected void onPostExecute(List<Outage> outages) {
            outagesLoadActive = false;
        }
    }

}