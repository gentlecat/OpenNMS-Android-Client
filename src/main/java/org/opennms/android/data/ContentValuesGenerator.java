package org.opennms.android.data;

import android.content.ContentValues;

import org.opennms.android.data.api.model.Alarm;
import org.opennms.android.data.api.model.Event;
import org.opennms.android.data.api.model.Node;
import org.opennms.android.data.api.model.Outage;
import org.opennms.android.data.storage.Contract;

import java.util.ArrayList;
import java.util.List;

public final class ContentValuesGenerator {

    public static ArrayList<ContentValues> fromNodes(List<Node> nodes) {
        ArrayList<ContentValues> valuesArray = new ArrayList<>();
        if (nodes != null) {
            for (Node node : nodes) {
                valuesArray.add(generate(node));
            }
        }
        return valuesArray;
    }

    public static ContentValues generate(Node node) {
        ContentValues values = new ContentValues();
        values.put(Contract.Nodes._ID, node.id);
        values.put(Contract.Nodes.LABEL, node.label);
        values.put(Contract.Nodes.CREATED_TIME, node.createTime.toString());
        values.put(Contract.Nodes.LABEL_SOURCE, node.labelSource);
        if (node.sysContact != null) values.put(Contract.Nodes.SYS_CONTACT, node.sysContact);
        if (node.sysDescription != null)
            values.put(Contract.Nodes.DESCRIPTION, node.sysDescription);
        if (node.sysLocation != null) values.put(Contract.Nodes.LOCATION, node.sysLocation);
        if (node.sysObjectId != null) values.put(Contract.Nodes.SYS_OBJECT_ID, node.sysObjectId);
        return values;
    }

    public static ArrayList<ContentValues> fromEvents(List<Event> events) {
        ArrayList<ContentValues> valuesArray = new ArrayList<>();
        if (events != null) {
            for (Event event : events) {
                valuesArray.add(generate(event));
            }
        }
        return valuesArray;
    }

    public static ContentValues generate(Event event) {
        ContentValues values = new ContentValues();
        values.put(Contract.Events._ID, event.id);
        if (event.description != null) values.put(Contract.Events.DESCRIPTION, event.description);
        if (event.logMessage != null) values.put(Contract.Events.LOG_MESSAGE, event.logMessage);
        values.put(Contract.Events.SEVERITY, event.severity);
        if (event.host != null) values.put(Contract.Events.HOST, event.host);
        if (event.ipAddress != null) values.put(Contract.Events.IP_ADDRESS, event.ipAddress);
        if (event.nodeId != 0) values.put(Contract.Events.NODE_ID, event.nodeId);
        if (event.nodeLabel != null) values.put(Contract.Events.NODE_LABEL, event.nodeLabel);
        values.put(Contract.Events.CREATE_TIME, event.createTime.toString());
        if (event.serviceType != null) {
            values.put(Contract.Events.SERVICE_TYPE_ID, event.serviceType.id);
            values.put(Contract.Events.SERVICE_TYPE_NAME, event.serviceType.name);
        }
        return values;
    }

    public static ArrayList<ContentValues> fromAlarms(List<Alarm> alarms) {
        ArrayList<ContentValues> valuesArray = new ArrayList<>();
        if (alarms != null) {
            for (Alarm alarm : alarms) {
                valuesArray.add(generate(alarm));
            }
        }
        return valuesArray;
    }

    public static ContentValues generate(Alarm alarm) {
        ContentValues values = new ContentValues();
        values.put(Contract.Alarms._ID, alarm.id);
        values.put(Contract.Alarms.SEVERITY, alarm.severity);
        if (alarm.ackUser != null) values.put(Contract.Alarms.ACK_USER, alarm.ackUser);
        if (alarm.ackTime != null) values.put(Contract.Alarms.ACK_TIME, alarm.ackTime.toString());
        if (alarm.logMessage != null) values.put(Contract.Alarms.LOG_MESSAGE, alarm.logMessage);
        if (alarm.description != null) values.put(Contract.Alarms.DESCRIPTION, alarm.description);
        if (alarm.firstEventTime != null)
            values.put(Contract.Alarms.FIRST_EVENT_TIME, alarm.firstEventTime.toString());
        if (alarm.lastEventTime != null) {
            values.put(Contract.Alarms.LAST_EVENT_TIME, alarm.lastEventTime.toString());
            values.put(Contract.Alarms.LAST_EVENT_ID, alarm.lastEvent.id);
            values.put(Contract.Alarms.LAST_EVENT_SEVERITY, alarm.lastEvent.severity);
        }
        if (alarm.nodeId != 0) values.put(Contract.Alarms.NODE_ID, alarm.nodeId);
        if (alarm.nodeLabel != null) values.put(Contract.Alarms.NODE_LABEL, alarm.nodeLabel);
        if (alarm.serviceType != null) {
            values.put(Contract.Alarms.SERVICE_TYPE_ID, alarm.serviceType.id);
            values.put(Contract.Alarms.SERVICE_TYPE_NAME, alarm.serviceType.name);
        }
        return values;
    }

    public static ArrayList<ContentValues> fromOutages(List<Outage> outages) {
        ArrayList<ContentValues> valuesArray = new ArrayList<>();
        if (outages != null) {
            for (Outage outage : outages) {
                valuesArray.add(generate(outage));
            }
        }
        return valuesArray;
    }

    public static ContentValues generate(Outage outage) {
        ContentValues values = new ContentValues();
        values.put(Contract.Outages._ID, outage.id);
        values.put(Contract.Outages.IP_ADDRESS, outage.ipAddress);
        // TODO: Add IP_INTERFACE_ID
        if (outage.serviceLostEvent != null) {
            values.put(Contract.Outages.SERVICE_ID, outage.serviceLostEvent.id);
            if (outage.serviceLostEvent.serviceType != null) {
                values.put(Contract.Outages.SERVICE_TYPE_ID, outage.serviceLostEvent.serviceType.id);
                values.put(Contract.Outages.SERVICE_TYPE_NAME, outage.serviceLostEvent.serviceType.name);
            }
            values.put(Contract.Outages.NODE_ID, outage.serviceLostEvent.nodeId);
            values.put(Contract.Outages.NODE_LABEL, outage.serviceLostEvent.nodeLabel);
            values.put(Contract.Outages.SERVICE_LOST_TIME, outage.serviceLostEvent.createTime.toString());
            values.put(Contract.Outages.SERVICE_LOST_EVENT_ID, outage.serviceLostEvent.id);
        }
        if (outage.serviceRegainedEvent != null) {
            values.put(Contract.Outages.SERVICE_REGAINED_EVENT_ID, outage.serviceRegainedEvent.id);
            values.put(Contract.Outages.SERVICE_REGAINED_TIME, outage.serviceRegainedEvent.createTime.toString());
        }
        return values;
    }

}
