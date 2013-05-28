package org.opennms.android.dao;

public class Columns {

    public interface NodeColumns {
        public static final String TABLE_NODES_ID = "_id";
        public static final String COL_NODE_ID = "node_id";
        public static final String COL_TYPE = "type";
        public static final String COL_LABEL = "label";
        public static final String COL_CREATED_TIME = "created_time";
        public static final String COL_LABEL_SOURCE = "label_source";
        public static final String COL_SYS_CONTACT = "sys_contact";
    }

    public interface AlarmColumns {
        public static final String TABLE_ALARMS_ID = "_id";
        public static final String COL_ALARM_ID = "alarm_id";
        public static final String COL_SEVERITY = "severity";
        public static final String COL_DESCRIPTION = "description";
        public static final String COL_LOG_MESSAGE = "log_message";
    }

    public interface OutageColumns {
        public static final String TABLE_OUTAGES_ID = "_id";
        public static final String COL_OUTAGE_ID = "outage_id";
        public static final String COL_IP_ADDRESS = "ip_address";
        public static final String COL_IF_REGAINED_SERVICE = "if_regained_service";
        public static final String COL_SERVICE_TYPE_NAME = "service_type_name";
        public static final String COL_IF_LOST_SERVICE = "if_lost_service";
    }

    public interface EventColumns {
        public static final String TABLE_EVENT_ID = "_id";
        public static final String COL_EVENT_ID = "event_id";
        public static final String COL_SEVERITY = "severity";
        public static final String COL_LOG_MESSAGE = "logMessage";
        public static final String COL_DESCRIPTION = "description";
        public static final String COL_HOST = "host";
        public static final String COL_IP_ADDRESS = "ipAddress";
        public static final String COL_NODE_ID = "nodeId";
        public static final String COL_NODE_LABEL = "nodeLabel";
    }

}
