package org.opennms.android.ui;

import android.content.Context;
import android.content.Intent;

import org.opennms.android.ui.alarms.AlarmDetailsActivity;
import org.opennms.android.ui.events.EventDetailsActivity;
import org.opennms.android.ui.nodes.NodeDetailsActivity;
import org.opennms.android.ui.outages.OutageDetailsActivity;

public class ActivityUtils {

    public static void showNodeDetails(Context context, long nodeId) {
        // TODO: Adjust for tablets
        Intent intent = new Intent(context, NodeDetailsActivity.class);
        intent.putExtra(NodeDetailsActivity.EXTRA_NODE_ID, nodeId);
        context.startActivity(intent);
    }

    public static void showAlarmDetails(Context context, long alarmId) {
        // TODO: Adjust for tablets
        Intent intent = new Intent(context, AlarmDetailsActivity.class);
        intent.putExtra(AlarmDetailsActivity.EXTRA_ALARM_ID, alarmId);
        context.startActivity(intent);
    }

    public static void showOutageDetails(Context context, long outageId) {
        // TODO: Adjust for tablets
        Intent intent = new Intent(context, OutageDetailsActivity.class);
        intent.putExtra(OutageDetailsActivity.EXTRA_OUTAGE_ID, outageId);
        context.startActivity(intent);
    }

    public static void showEventDetails(Context context, long eventId) {
        // TODO: Adjust for tablets
        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, eventId);
        context.startActivity(intent);
    }

}
