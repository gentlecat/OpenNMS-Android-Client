package org.opennms.android.dao;

public class Columns {

    public interface NodeColumns {
        public static final String TABLE_ID = "_id";
        // Node details
        public static final String NODE_ID = "node_id";
        public static final String TYPE = "type";
        public static final String NAME = "name";
        public static final String CREATED_TIME = "created_time";
        public static final String LABEL_SOURCE = "label_source";
        public static final String SYS_CONTACT = "sys_contact";
        public static final String DESCRIPTION = "description";
        public static final String LOCATION = "location";
    }

    public interface AlarmColumns {
        public static final String TABLE_ID = "_id";
        // Alarm details
        public static final String ALARM_ID = "alarm_id";
        public static final String SEVERITY = "severity";
        public static final String DESCRIPTION = "description";
        public static final String LOG_MESSAGE = "log_message";
        // Event info
        public static final String FIRST_EVENT_TIME = "first_event_time";
        public static final String LAST_EVENT_TIME = "last_event_time";
        public static final String LAST_EVENT_ID = "last_event_id";
        public static final String LAST_EVENT_SEVERITY = "last_event_severity";
        // Node info
        public static final String NODE_ID = "node_id";
        public static final String NODE_LABEL = "node_label";
        // Service type info
        public static final String SERVICE_TYPE_ID = "service_type_id";
        public static final String SERVICE_TYPE_NAME = "service_type_name";
    }

    public interface OutageColumns {
        public static final String TABLE_ID = "_id";
        // Outage details
        public static final String OUTAGE_ID = "outage_id";
        public static final String IP_ADDRESS = "ip_address";
        // Service
        public static final String SERVICE_ID = "service_id";
        public static final String IP_INTERFACE_ID = "ip_interface_id";
        public static final String SERVICE_TYPE_ID = "service_type_id";
        public static final String SERVICE_TYPE_NAME = "service_type_name";
        // Service lost event
        public static final String SERVICE_LOST_TIME = "service_lost_time";
        public static final String SERVICE_LOST_EVENT_ID = "service_lost_event_id";
        // Service regained event
        public static final String SERVICE_REGAINED_TIME = "service_regained_time";
        public static final String SERVICE_REGAINED_EVENT_ID = "service_regained_event_id";
    }

    public interface EventColumns {
        public static final String TABLE_ID = "_id";
        // Event details
        public static final String EVENT_ID = "event_id";
        public static final String SEVERITY = "severity";
        public static final String LOG_MESSAGE = "log_message";
        public static final String DESCRIPTION = "description";
        public static final String HOST = "host";
        public static final String IP_ADDRESS = "ip_address";
        public static final String CREATE_TIME = "create_time";
        // Node info
        public static final String NODE_ID = "node_id";
        public static final String NODE_LABEL = "node_label";
        // Service type info
        public static final String SERVICE_TYPE_ID = "service_type_id";
        public static final String SERVICE_TYPE_NAME = "service_type_name";
    }

}
