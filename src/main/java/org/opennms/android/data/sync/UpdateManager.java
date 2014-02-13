package org.opennms.android.data.sync;

import android.os.AsyncTask;
import android.util.Log;

import org.opennms.android.App;

import javax.inject.Inject;

public class UpdateManager {

  private static final String TAG = "UpdateManager";
  @Inject Updater updater;

  public UpdateManager(App app) {
    app.inject(this);
  }

  public enum UpdateType {NODES, ALARMS, EVENTS, OUTAGES}

  boolean nodesUpdating = false;
  boolean alarmsUpdating = false;
  boolean eventsUpdating = false;
  boolean outagesUpdating = false;

  public boolean isUpdating(UpdateType updateType) {
    switch (updateType) {
      case NODES:
        return nodesUpdating;
      case ALARMS:
        return alarmsUpdating;
      case EVENTS:
        return eventsUpdating;
      case OUTAGES:
        return outagesUpdating;
    }
    return false;
  }

  public void startUpdating(UpdateType updateType, int limit, int offset) {
    if (isUpdating(updateType)) {
      Log.i(TAG, "Already updating.");
      return;
    }
    switch (updateType) {
      case NODES:
        new NodesUpdater(limit, offset).execute();
        break;
      case ALARMS:
        new AlarmsUpdater(limit, offset).execute();
        break;
      case EVENTS:
        new EventsUpdater(limit, offset).execute();
        break;
      case OUTAGES:
        new OutagesUpdater(limit, offset).execute();
        break;
    }
  }

  class NodesUpdater extends AsyncTask<Void, Void, Boolean> {

    private int limit, offset;

    public NodesUpdater(int limit, int offset) {
      this.limit = limit;
      this.offset = offset;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      nodesUpdating = true;
    }

    protected Boolean doInBackground(Void... voids) {
      return updater.updateNodes(limit, offset);
    }

    protected void onPostExecute(Boolean success) {
      nodesUpdating = false;
    }
  }

  class AlarmsUpdater extends AsyncTask<Void, Void, Boolean> {

    private int limit, offset;

    public AlarmsUpdater(int limit, int offset) {
      this.limit = limit;
      this.offset = offset;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      alarmsUpdating = true;
    }

    protected Boolean doInBackground(Void... voids) {
      return updater.updateAlarms(limit, offset);
    }

    protected void onPostExecute(Boolean success) {
      alarmsUpdating = false;
    }
  }

  class EventsUpdater extends AsyncTask<Void, Void, Boolean> {

    private int limit, offset;

    public EventsUpdater(int limit, int offset) {
      this.limit = limit;
      this.offset = offset;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      eventsUpdating = true;
    }

    protected Boolean doInBackground(Void... voids) {
      return updater.updateEvents(limit, offset);
    }

    protected void onPostExecute(Boolean success) {
      eventsUpdating = false;
    }
  }

  class OutagesUpdater extends AsyncTask<Void, Void, Boolean> {

    private int limit, offset;

    public OutagesUpdater(int limit, int offset) {
      this.limit = limit;
      this.offset = offset;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      outagesUpdating = true;
    }

    protected Boolean doInBackground(Void... voids) {
      return updater.updateOutages(limit, offset);
    }

    protected void onPostExecute(Boolean success) {
      outagesUpdating = false;
    }
  }

}