package org.opennms.android.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {

    public static final String CONTENT_AUTHORITY = "org.opennms.android.sync.AlarmsSyncAdapter";
    public static final String PATH_NODES = Tables.NODES;
    public static final String PATH_ALARMS = Tables.ALARMS;
    public static final String PATH_OUTAGES = Tables.OUTAGES;
    public static final String PATH_EVENTS = Tables.EVENTS;
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static interface Tables {

        public static final String NODES = "nodes";
        public static final String ALARMS = "alarms";
        public static final String OUTAGES = "outages";
        public static final String EVENTS = "event";
    }

    interface NodeColumns {
        String NAME = "name";
        String CREATED_TIME = "created_time";
        String LABEL_SOURCE = "label_source";
        String DESCRIPTION = "description";
        String LOCATION = "location";
        String CONTACT = "contact";
        String SYS_OBJECT_ID = "sys_object_id";
    }

    interface AlarmColumns {
        String SEVERITY = "severity";
        String DESCRIPTION = "description";
        String LOG_MESSAGE = "log_message";
        String ACK_USER = "ack_user";
        String ACK_TIME = "ack_time";
        // Event info
        String FIRST_EVENT_TIME = "first_event_time";
        String LAST_EVENT_TIME = "last_event_time";
        String LAST_EVENT_ID = "last_event_id";
        String LAST_EVENT_SEVERITY = "last_event_severity";
        // Node info
        String NODE_ID = "node_id";
        String NODE_LABEL = "node_label";
        // Service type info
        String SERVICE_TYPE_ID = "service_type_id";
        String SERVICE_TYPE_NAME = "service_type_name";
    }

    interface OutageColumns {
        String IP_ADDRESS = "ip_address";
        // Service
        String SERVICE_ID = "service_id";
        String IP_INTERFACE_ID = "ip_interface_id";
        String SERVICE_TYPE_ID = "service_type_id";
        String SERVICE_TYPE_NAME = "service_type_name";
        // Node
        String NODE_ID = "node_id";
        String NODE_LABEL = "node_label";
        // Service lost event
        String SERVICE_LOST_TIME = "service_lost_time";
        String SERVICE_LOST_EVENT_ID = "service_lost_event_id";
        // Service regained event
        String SERVICE_REGAINED_TIME = "service_regained_time";
        String SERVICE_REGAINED_EVENT_ID = "service_regained_event_id";
    }

    interface EventColumns {
        String SEVERITY = "severity";
        String LOG_MESSAGE = "log_message";
        String DESCRIPTION = "description";
        String HOST = "host";
        String IP_ADDRESS = "ip_address";
        String CREATE_TIME = "create_time";
        // Node info
        String NODE_ID = "node_id";
        String NODE_LABEL = "node_label";
        // Service type info
        String SERVICE_TYPE_ID = "service_type_id";
        String SERVICE_TYPE_NAME = "service_type_name";
    }

    public static class Nodes implements NodeColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NODES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.opennms.node";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.opennms.node";
    }

    public static class Alarms implements AlarmColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALARMS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.opennms.alarm";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.opennms.alarm";
    }

    public static class Outages implements OutageColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_OUTAGES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.opennms.outage";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.opennms.outage";
    }

    public static class Events implements EventColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENTS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.opennms.event";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.opennms.event";
    }

}
