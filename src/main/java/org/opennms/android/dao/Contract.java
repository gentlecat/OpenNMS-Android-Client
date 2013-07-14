package org.opennms.android.dao;

import android.provider.BaseColumns;

public final class Contract {

    public Contract() {
    }

    public static abstract class Nodes implements BaseColumns {
        public static final String TABLE_NAME = "node";
        /*
         * Columns
         */
        public static final String COLUMN_NODE_ID = "node_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CREATED_TIME = "created_time";
        public static final String COLUMN_LABEL_SOURCE = "label_source";
        public static final String COLUMN_SYS_CONTACT = "sys_contact";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_LOCATION = "location";
    }

    public static abstract class Alarms implements BaseColumns {
        public static final String TABLE_NAME = "alarm";
        /*
         * Columns
         */
        // Alarm details
        public static final String COLUMN_ALARM_ID = "alarm_id";
        public static final String COLUMN_SEVERITY = "severity";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_LOG_MESSAGE = "log_message";
        // Event info
        public static final String COLUMN_FIRST_EVENT_TIME = "first_event_time";
        public static final String COLUMN_LAST_EVENT_TIME = "last_event_time";
        public static final String COLUMN_LAST_EVENT_ID = "last_event_id";
        public static final String COLUMN_LAST_EVENT_SEVERITY = "last_event_severity";
        // Node info
        public static final String COLUMN_NODE_ID = "node_id";
        public static final String COLUMN_NODE_LABEL = "node_label";
        // Service type info
        public static final String COLUMN_SERVICE_TYPE_ID = "service_type_id";
        public static final String COLUMN_SERVICE_TYPE_NAME = "service_type_name";


    }

    public static abstract class Outages implements BaseColumns {
        public static final String TABLE_NAME = "outage";
        /*
         * Columns
         */
        // Outage details
        public static final String COLUMN_OUTAGE_ID = "outage_id";
        public static final String COLUMN_IP_ADDRESS = "ip_address";
        // Service
        public static final String COLUMN_SERVICE_ID = "service_id";
        public static final String COLUMN_IP_INTERFACE_ID = "ip_interface_id";
        public static final String COLUMN_SERVICE_TYPE_ID = "service_type_id";
        public static final String COLUMN_SERVICE_TYPE_NAME = "service_type_name";
        // Service lost event
        public static final String COLUMN_SERVICE_LOST_TIME = "service_lost_time";
        public static final String COLUMN_SERVICE_LOST_EVENT_ID = "service_lost_event_id";
        // Service regained event
        public static final String COLUMN_SERVICE_REGAINED_TIME = "service_regained_time";
        public static final String COLUMN_SERVICE_REGAINED_EVENT_ID = "service_regained_event_id";
    }

    public static abstract class Events implements BaseColumns {
        public static final String TABLE_NAME = "event";
        /*
         * Columns
         */
        // Event details
        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_SEVERITY = "severity";
        public static final String COLUMN_LOG_MESSAGE = "log_message";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_HOST = "host";
        public static final String COLUMN_IP_ADDRESS = "ip_address";
        public static final String COLUMN_CREATE_TIME = "create_time";
        // Node info
        public static final String COLUMN_NODE_ID = "node_id";
        public static final String COLUMN_NODE_LABEL = "node_label";
        // Service type info
        public static final String COLUMN_SERVICE_TYPE_ID = "service_type_id";
        public static final String COLUMN_SERVICE_TYPE_NAME = "service_type_name";
    }

}
