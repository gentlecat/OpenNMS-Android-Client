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
            + NodeColumns.TABLE_NODES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NodeColumns.COL_NODE_ID + " INTEGER UNIQUE, "
            + NodeColumns.COL_TYPE + " TEXT, "
            + NodeColumns.COL_LABEL + " TEXT, "
            + NodeColumns.COL_CREATED_TIME + " TEXT, "
            + NodeColumns.COL_LABEL_SOURCE + " TEXT, "
            + NodeColumns.COL_SYS_CONTACT + " TEXT"
            + ");";
    private static final String CREATE_OUTAGES_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.OUTAGES
            + " ("
            + OutageColumns.TABLE_OUTAGES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + OutageColumns.COL_OUTAGE_ID + " INTEGER UNIQUE, "
            + OutageColumns.COL_IP_ADDRESS + " TEXT, "
            + OutageColumns.COL_IF_REGAINED_SERVICE + " TEXT, "
            + OutageColumns.COL_SERVICE_TYPE_NAME + " TEXT, "
            + OutageColumns.COL_IF_LOST_SERVICE + " TEXT"
            + ");";
    private static final String CREATE_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.EVENTS
            + " ("
            + EventColumns.TABLE_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + EventColumns.COL_EVENT_ID + " INTEGER UNIQUE, "
            + EventColumns.COL_SEVERITY + " TEXT, "
            + EventColumns.COL_LOG_MESSAGE + " TEXT, "
            + EventColumns.COL_DESCRIPTION + " TEXT, "
            + EventColumns.COL_HOST + " TEXT, "
            + EventColumns.COL_IP_ADDRESS + " TEXT, "
            + EventColumns.COL_NODE_ID + " INTEGER, "
            + EventColumns.COL_NODE_LABEL + " TEXT"
            + ");";
    private static final String CREATE_ALARMS_TABLE = "CREATE TABLE IF NOT EXISTS " + Tables.ALARMS
            + " ("
            + AlarmColumns.TABLE_ALARMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AlarmColumns.COL_ALARM_ID + " INTEGER UNIQUE, "
            + AlarmColumns.COL_SEVERITY + " TEXT, "
            + AlarmColumns.COL_DESCRIPTION + " TEXT, "
            + AlarmColumns.COL_LOG_MESSAGE + " TEXT"
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