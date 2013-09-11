package org.opennms.android.sync;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.opennms.android.net.DataLoader;
import org.opennms.android.net.Response;
import org.opennms.android.parsing.AlarmsParser;
import org.opennms.android.parsing.EventsParser;
import org.opennms.android.parsing.NodesParser;
import org.opennms.android.parsing.OutagesParser;
import org.opennms.android.provider.Contract;

import java.util.ArrayList;

public class LoadManager extends Service {

    private static final String TAG = "LoadManager";

    @Override
    public void onCreate() {
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

    class NodesLoader extends AsyncTask<Void, Void, Response> {
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

        protected Response doInBackground(Void... voids) {
            Response response;
            try {
                response = new DataLoader(getApplicationContext()).loadNodes(limit, offset);
            } catch (Exception e) {
                Log.e(TAG, "Error occurred during nodes loading", e);
                return null;
            }

            /** Updating database records */
            ArrayList<ContentValues> values = NodesParser.parseMultiple(response.getMessage());
            getContentResolver().bulkInsert(Contract.Nodes.CONTENT_URI,
                    values.toArray(new ContentValues[values.size()]));

            return response;
        }

        protected void onPostExecute(Response response) {
            nodesLoadActive = false;
        }
    }

    class AlarmsLoader extends AsyncTask<Void, Void, Response> {
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

        protected Response doInBackground(Void... voids) {
            Response response;
            try {
                response = new DataLoader(getApplicationContext()).loadAlarms(limit, offset);
            } catch (Exception e) {
                Log.e(TAG, "Error occurred during alarms loading", e);
                return null;
            }

            /** Updating database records */
            ArrayList<ContentValues> values = AlarmsParser.parseMultiple(response.getMessage());
            getContentResolver().bulkInsert(Contract.Alarms.CONTENT_URI,
                    values.toArray(new ContentValues[values.size()]));

            return response;
        }

        protected void onPostExecute(Response response) {
            alarmsLoadActive = false;
        }
    }

    class EventsLoader extends AsyncTask<Void, Void, Response> {
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

        protected Response doInBackground(Void... voids) {
            Response response;
            try {
                response = new DataLoader(getApplicationContext()).loadEvents(limit, offset);
            } catch (Exception e) {
                Log.e(TAG, "Error occurred during events loading", e);
                return null;
            }

            /** Updating database records */
            ArrayList<ContentValues> values = EventsParser.parseMultiple(response.getMessage());
            getContentResolver().bulkInsert(Contract.Events.CONTENT_URI,
                    values.toArray(new ContentValues[values.size()]));

            return response;
        }

        protected void onPostExecute(Response response) {
            eventsLoadActive = false;
        }
    }

    class OutagesLoader extends AsyncTask<Void, Void, Response> {
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

        protected Response doInBackground(Void... voids) {
            Response response;
            try {
                response = new DataLoader(getApplicationContext()).loadOutages(limit, offset);
            } catch (Exception e) {
                Log.e(TAG, "Error occurred during outages loading", e);
                return null;
            }

            /** Updating database records */
            ArrayList<ContentValues> values = OutagesParser.parseMultiple(response.getMessage());
            getContentResolver().bulkInsert(Contract.Outages.CONTENT_URI,
                    values.toArray(new ContentValues[values.size()]));

            return response;
        }

        protected void onPostExecute(Response response) {
            outagesLoadActive = false;
        }
    }

}