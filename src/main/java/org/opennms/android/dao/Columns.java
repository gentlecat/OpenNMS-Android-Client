package org.opennms.android.dao;

public class Columns {

    public interface NodeColumns {
        public static final String TABLE_ID = "_id";
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
        public static final String ALARM_ID = "alarm_id";
        public static final String SEVERITY = "severity";
        public static final String DESCRIPTION = "description";
        public static final String LOG_MESSAGE = "log_message";
    }

    public interface OutageColumns {
        public static final String TABLE_ID = "_id";
        public static final String OUTAGE_ID = "outage_id";
        public static final String IP_ADDRESS = "ip_address";
        public static final String IF_REGAINED_SERVICE = "if_regained_service";
        public static final String SERVICE_TYPE_NAME = "service_type_name";
        public static final String IF_LOST_SERVICE = "if_lost_service";
    }

    public interface EventColumns {
        public static final String TABLE_ID = "_id";
        public static final String EVENT_ID = "event_id";
        public static final String SEVERITY = "severity";
        public static final String LOG_MESSAGE = "logMessage";
        public static final String DESCRIPTION = "description";
        public static final String HOST = "host";
        public static final String IP_ADDRESS = "ipAddress";
        public static final String NODE_ID = "nodeId";
        public static final String NODE_LABEL = "nodeLabel";
        public static final String CREATE_TIME = "createTime";
    }

}
