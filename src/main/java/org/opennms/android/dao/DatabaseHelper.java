package org.opennms.android.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.opennms.android.dao.Contract.Alarms;
import org.opennms.android.dao.Contract.Events;
import org.opennms.android.dao.Contract.Nodes;
import org.opennms.android.dao.Contract.Outages;

public class DatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "OpenNMS.db";
    /*
     * SQL
     */
    private static final String SQL_CREATE_TABLE_NODES = "CREATE TABLE IF NOT EXISTS " + Nodes.TABLE_NAME
            + " ("
            + Nodes._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Nodes.COLUMN_NODE_ID + " INTEGER UNIQUE, "
            + Nodes.COLUMN_TYPE + " TEXT, "
            + Nodes.COLUMN_NAME + " TEXT, "
            + Nodes.COLUMN_CREATED_TIME + " TEXT, "
            + Nodes.COLUMN_LABEL_SOURCE + " TEXT, "
            + Nodes.COLUMN_SYS_CONTACT + " TEXT, "
            + Nodes.COLUMN_DESCRIPTION + " TEXT, "
            + Nodes.COLUMN_LOCATION + " TEXT"
            + ");";
    private static final String SQL_DROP_TABLE_NODES = "DROP TABLE IF EXISTS " + Nodes.TABLE_NAME;
    private static final String SQL_CREATE_TABLE_OUTAGES = "CREATE TABLE IF NOT EXISTS " + Outages.TABLE_NAME
            + " ("
            + Outages._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Outages.COLUMN_OUTAGE_ID + " INTEGER UNIQUE, "
            + Outages.COLUMN_IP_ADDRESS + " TEXT, "
            + Outages.COLUMN_IP_INTERFACE_ID + " INTEGER, "
            + Outages.COLUMN_SERVICE_ID + " INTEGER, "
            + Outages.COLUMN_SERVICE_TYPE_NAME + " TEXT, "
            + Outages.COLUMN_SERVICE_TYPE_ID + " INTEGER, "
            + Outages.COLUMN_SERVICE_LOST_TIME + " TEXT, "
            + Outages.COLUMN_SERVICE_LOST_EVENT_ID + " INTEGER, "
            + Outages.COLUMN_SERVICE_REGAINED_TIME + " TEXT, "
            + Outages.COLUMN_SERVICE_REGAINED_EVENT_ID + " INTEGER"
            + ");";
    private static final String SQL_DROP_TABLE_OUTAGES = "DROP TABLE IF EXISTS " + Outages.TABLE_NAME;
    private static final String SQL_CREATE_TABLE_EVENTS = "CREATE TABLE IF NOT EXISTS " + Events.TABLE_NAME
            + " ("
            + Events._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Events.COLUMN_EVENT_ID + " INTEGER UNIQUE, "
            + Events.COLUMN_SEVERITY + " TEXT, "
            + Events.COLUMN_LOG_MESSAGE + " TEXT, "
            + Events.COLUMN_DESCRIPTION + " TEXT, "
            + Events.COLUMN_HOST + " TEXT, "
            + Events.COLUMN_IP_ADDRESS + " TEXT, "
            + Events.COLUMN_NODE_ID + " INTEGER, "
            + Events.COLUMN_NODE_LABEL + " TEXT, "
            + Events.COLUMN_SERVICE_TYPE_ID + " INTEGER, "
            + Events.COLUMN_SERVICE_TYPE_NAME + " TEXT, "
            + Events.COLUMN_CREATE_TIME + " TEXT"
            + ");";
    private static final String SQL_DROP_TABLE_EVENTS = "DROP TABLE IF EXISTS " + Events.TABLE_NAME;
    private static final String SQL_CREATE_TABLE_ALARMS = "CREATE TABLE IF NOT EXISTS " + Alarms.TABLE_NAME
            + " ("
            + Alarms._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Alarms.COLUMN_ALARM_ID + " INTEGER UNIQUE, "
            + Alarms.COLUMN_SEVERITY + " TEXT, "
            + Alarms.COLUMN_DESCRIPTION + " TEXT, "
            + Alarms.COLUMN_FIRST_EVENT_TIME + " TEXT, "
            + Alarms.COLUMN_LAST_EVENT_TIME + " TEXT, "
            + Alarms.COLUMN_LAST_EVENT_ID + " INTEGER, "
            + Alarms.COLUMN_LAST_EVENT_SEVERITY + " TEXT, "
            + Alarms.COLUMN_NODE_ID + " INTEGER, "
            + Alarms.COLUMN_NODE_LABEL + " TEXT, "
            + Alarms.COLUMN_SERVICE_TYPE_ID + " INTEGER, "
            + Alarms.COLUMN_SERVICE_TYPE_NAME + " TEXT, "
            + Alarms.COLUMN_LOG_MESSAGE + " TEXT"
            + ");";
    private static final String SQL_DROP_TABLE_ALARMS = "DROP TABLE IF EXISTS " + Alarms.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_NODES);
        db.execSQL(SQL_CREATE_TABLE_OUTAGES);
        db.execSQL(SQL_CREATE_TABLE_EVENTS);
        db.execSQL(SQL_CREATE_TABLE_ALARMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE_NODES);
        db.execSQL(SQL_DROP_TABLE_OUTAGES);
        db.execSQL(SQL_DROP_TABLE_EVENTS);
        db.execSQL(SQL_DROP_TABLE_ALARMS);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_NODES);
        db.execSQL(SQL_CREATE_TABLE_OUTAGES);
        db.execSQL(SQL_CREATE_TABLE_EVENTS);
        db.execSQL(SQL_CREATE_TABLE_ALARMS);
    }

}