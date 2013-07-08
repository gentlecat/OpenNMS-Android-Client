package org.opennms.android.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.opennms.android.dao.Columns.AlarmColumns;
import org.opennms.android.dao.Columns.EventColumns;
import org.opennms.android.dao.Columns.NodeColumns;
import org.opennms.android.dao.Columns.OutageColumns;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_NODES_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.NODES
            + " ("
            + NodeColumns.TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NodeColumns.NODE_ID + " INTEGER UNIQUE, "
            + NodeColumns.TYPE + " TEXT, "
            + NodeColumns.NAME + " TEXT, "
            + NodeColumns.CREATED_TIME + " TEXT, "
            + NodeColumns.LABEL_SOURCE + " TEXT, "
            + NodeColumns.SYS_CONTACT + " TEXT, "
            + NodeColumns.DESCRIPTION + " TEXT, "
            + NodeColumns.LOCATION + " TEXT"
            + ");";
    private static final String CREATE_OUTAGES_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.OUTAGES
            + " ("
            + OutageColumns.TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + OutageColumns.OUTAGE_ID + " INTEGER UNIQUE, "
            + OutageColumns.IP_ADDRESS + " TEXT, "
            + OutageColumns.IP_INTERFACE_ID + " INTEGER, "
            + OutageColumns.SERVICE_ID + " INTEGER, "
            + OutageColumns.SERVICE_TYPE_NAME + " TEXT, "
            + OutageColumns.SERVICE_TYPE_ID + " INTEGER, "
            + OutageColumns.SERVICE_LOST_TIME + " TEXT, "
            + OutageColumns.SERVICE_LOST_EVENT_ID + " INTEGER, "
            + OutageColumns.SERVICE_REGAINED_TIME + " TEXT, "
            + OutageColumns.SERVICE_REGAINED_EVENT_ID + " INTEGER"
            + ");";
    private static final String CREATE_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.EVENTS
            + " ("
            + EventColumns.TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + EventColumns.EVENT_ID + " INTEGER UNIQUE, "
            + EventColumns.SEVERITY + " TEXT, "
            + EventColumns.LOG_MESSAGE + " TEXT, "
            + EventColumns.DESCRIPTION + " TEXT, "
            + EventColumns.HOST + " TEXT, "
            + EventColumns.IP_ADDRESS + " TEXT, "
            + EventColumns.NODE_ID + " INTEGER, "
            + EventColumns.NODE_LABEL + " TEXT, "
            + EventColumns.SERVICE_TYPE_ID + " INTEGER, "
            + EventColumns.SERVICE_TYPE_NAME + " TEXT, "
            + EventColumns.CREATE_TIME + " TEXT"
            + ");";
    private static final String CREATE_ALARMS_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.ALARMS
            + " ("
            + AlarmColumns.TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AlarmColumns.ALARM_ID + " INTEGER UNIQUE, "
            + AlarmColumns.SEVERITY + " TEXT, "
            + AlarmColumns.DESCRIPTION + " TEXT, "
            + AlarmColumns.FIRST_EVENT_TIME + " TEXT, "
            + AlarmColumns.LAST_EVENT_TIME + " TEXT, "
            + AlarmColumns.LAST_EVENT_ID + " INTEGER, "
            + AlarmColumns.LAST_EVENT_SEVERITY + " TEXT, "
            + AlarmColumns.NODE_ID + " INTEGER, "
            + AlarmColumns.NODE_LABEL + " TEXT, "
            + AlarmColumns.SERVICE_TYPE_ID + " INTEGER, "
            + AlarmColumns.SERVICE_TYPE_NAME + " TEXT, "
            + AlarmColumns.LOG_MESSAGE + " TEXT"
            + ");";

    public DatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NODES_TABLE);
        db.execSQL(CREATE_OUTAGES_TABLE);
        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(CREATE_ALARMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.NODES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.OUTAGES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ALARMS);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL(CREATE_NODES_TABLE);
        db.execSQL(CREATE_OUTAGES_TABLE);
        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(CREATE_ALARMS_TABLE);
    }

    public interface Tables {
        public static final String NODES = "nodes";
        public static final String OUTAGES = "outages";
        public static final String EVENTS = "event";
        public static final String ALARMS = "alarms";
    }

}