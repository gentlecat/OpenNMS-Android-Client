package org.opennms.android.data.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.util.Log;

import org.opennms.android.App;
import org.opennms.android.data.ContentValuesGenerator;
import org.opennms.android.data.api.ServerInterface;
import org.opennms.android.data.api.model.Alarm;
import org.opennms.android.data.api.model.Event;
import org.opennms.android.data.api.model.Node;
import org.opennms.android.data.api.model.Outage;
import org.opennms.android.data.storage.Contract;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class Updater {

  public static final String TAG = "Updater";
  @Inject ServerInterface server;
  @Inject ContentResolver contentResolver;

  public Updater(App app) {
    app.inject(this);
  }

  public boolean updateNodes(int limit, int offset) {
    List<Node> nodes;
    try {
      nodes = server.nodes(limit, offset).nodes;
    } catch (Exception e) {
      Log.e(TAG, "Error occurred during nodes update!", e);
      return false;
    }
    ArrayList<ContentValues> values = ContentValuesGenerator.fromNodes(nodes);
    contentResolver.bulkInsert(Contract.Nodes.CONTENT_URI,
                               values.toArray(new ContentValues[values.size()]));
    return true;
  }

  public boolean updateNode(long id) {
    Node node;
    try {
      node = server.node(id);
    } catch (Exception e) {
      Log.e(TAG, "Error occurred during node update!", e);
      return false;
    }
    ContentValues[] values = new ContentValues[1];
    values[0] = ContentValuesGenerator.generate(node);
    contentResolver.bulkInsert(Contract.Nodes.CONTENT_URI, values);
    return true;
  }

  public boolean updateAlarms(int limit, int offset) {
    List<Alarm> alarms;
    try {
      alarms = server.alarms(limit, offset).alarms;
    } catch (Exception e) {
      Log.e(TAG, "Error occurred during alarms update!", e);
      return false;
    }
    ArrayList<ContentValues> values = ContentValuesGenerator.fromAlarms(alarms);
    contentResolver.bulkInsert(Contract.Alarms.CONTENT_URI,
                               values.toArray(new ContentValues[values.size()]));
    return true;
  }

  public boolean updateAlarm(long id) {
    Alarm alarm;
    try {
      alarm = server.alarm(id);
    } catch (Exception e) {
      Log.e(TAG, "Error occurred during alarm update!", e);
      return false;
    }
    ContentValues[] values = new ContentValues[1];
    values[0] = ContentValuesGenerator.generate(alarm);
    contentResolver.bulkInsert(Contract.Alarms.CONTENT_URI, values);
    return true;
  }

  public boolean updateEvents(int limit, int offset) {
    List<Event> events;
    try {
      events = server.events(limit, offset).events;
    } catch (Exception e) {
      Log.e(TAG, "Error occurred during events update!", e);
      return false;
    }
    ArrayList<ContentValues> values = ContentValuesGenerator.fromEvents(events);
    contentResolver.bulkInsert(Contract.Events.CONTENT_URI,
                               values.toArray(new ContentValues[values.size()]));
    return true;
  }

  public boolean updateEvent(long id) {
    Event event;
    try {
      event = server.event(id);
    } catch (Exception e) {
      Log.e(TAG, "Error occurred during event update!", e);
      return false;
    }
    ContentValues[] values = new ContentValues[1];
    values[0] = ContentValuesGenerator.generate(event);
    contentResolver.bulkInsert(Contract.Events.CONTENT_URI, values);
    return true;
  }

  public boolean updateOutages(int limit, int offset) {
    List<Outage> outages;
    try {
      outages = server.outages(limit, offset).outages;
    } catch (Exception e) {
      Log.e(TAG, "Error occurred during events update!", e);
      return false;
    }
    ArrayList<ContentValues> values = ContentValuesGenerator.fromOutages(outages);
    contentResolver.bulkInsert(Contract.Outages.CONTENT_URI,
                               values.toArray(new ContentValues[values.size()]));
    return true;
  }

  public boolean updateOutage(long id) {
    Outage outage;
    try {
      outage = server.outage(id);
    } catch (Exception e) {
      Log.e(TAG, "Error occurred during event update!", e);
      return false;
    }
    ContentValues[] values = new ContentValues[1];
    values[0] = ContentValuesGenerator.generate(outage);
    contentResolver.bulkInsert(Contract.Outages.CONTENT_URI, values);
    return true;
  }

}