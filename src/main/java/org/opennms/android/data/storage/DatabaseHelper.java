package org.opennms.android.data.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";

    /**
     * If you change the database schema, you must increment the database version!
     */
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "OpenNMS.db";
    /**
     * SQL
     */
    private static final String SQL_CREATE_TABLE_NODES =
            "CREATE TABLE IF NOT EXISTS " + Contract.Tables.NODES
            + " ("
            + Contract.Nodes._ID + " INTEGER PRIMARY KEY, "
            + Contract.Nodes.LABEL + " TEXT, "
            + Contract.Nodes.CREATED_TIME + " TEXT, "
            + Contract.Nodes.LABEL_SOURCE + " TEXT, "
            + Contract.Nodes.SYS_CONTACT + " TEXT, "
            + Contract.Nodes.DESCRIPTION + " TEXT, "
            + Contract.Nodes.SYS_OBJECT_ID + " TEXT, "
            + Contract.Nodes.LOCATION + " TEXT"
            + ");";
    private static final String SQL_DROP_TABLE_NODES =
            "DROP TABLE IF EXISTS " + Contract.Tables.NODES;
    private static final String SQL_CREATE_TABLE_OUTAGES =
            "CREATE TABLE IF NOT EXISTS " + Contract.Tables.OUTAGES
            + " ("
            + Contract.Outages._ID + " INTEGER PRIMARY KEY, "
            + Contract.Outages.IP_ADDRESS + " TEXT, "
            + Contract.Outages.IP_INTERFACE_ID + " INTEGER, "
            + Contract.Outages.SERVICE_ID + " INTEGER, "
            + Contract.Outages.SERVICE_TYPE_NAME + " TEXT, "
            + Contract.Outages.SERVICE_TYPE_ID + " INTEGER, "
            + Contract.Outages.NODE_ID + " INTEGER, "
            + Contract.Outages.NODE_LABEL + " TEXT, "
            + Contract.Outages.SERVICE_LOST_TIME + " TEXT, "
            + Contract.Outages.SERVICE_LOST_EVENT_ID + " INTEGER, "
            + Contract.Outages.SERVICE_REGAINED_TIME + " TEXT, "
            + Contract.Outages.SERVICE_REGAINED_EVENT_ID + " INTEGER"
            + ");";
    private static final String SQL_DROP_TABLE_OUTAGES =
            "DROP TABLE IF EXISTS " + Contract.Tables.OUTAGES;
    private static final String SQL_CREATE_TABLE_EVENTS =
            "CREATE TABLE IF NOT EXISTS " + Contract.Tables.EVENTS
            + " ("
            + Contract.Events._ID + " INTEGER PRIMARY KEY, "
            + Contract.Events.SEVERITY + " TEXT, "
            + Contract.Events.LOG_MESSAGE + " TEXT, "
            + Contract.Events.DESCRIPTION + " TEXT, "
            + Contract.Events.HOST + " TEXT, "
            + Contract.Events.IP_ADDRESS + " TEXT, "
            + Contract.Events.NODE_ID + " INTEGER, "
            + Contract.Events.NODE_LABEL + " TEXT, "
            + Contract.Events.SERVICE_TYPE_ID + " INTEGER, "
            + Contract.Events.SERVICE_TYPE_NAME + " TEXT, "
            + Contract.Events.CREATE_TIME + " TEXT"
            + ");";
    private static final String SQL_DROP_TABLE_EVENTS =
            "DROP TABLE IF EXISTS " + Contract.Tables.EVENTS;
    private static final String SQL_CREATE_TABLE_ALARMS =
            "CREATE TABLE IF NOT EXISTS " + Contract.Tables.ALARMS
            + " ("
            + Contract.Alarms._ID + " INTEGER PRIMARY KEY, "
            + Contract.Alarms.SEVERITY + " TEXT, "
            + Contract.Alarms.ACK_USER + " TEXT, "
            + Contract.Alarms.ACK_TIME + " TEXT, "
            + Contract.Alarms.DESCRIPTION + " TEXT, "
            + Contract.Alarms.FIRST_EVENT_TIME + " TEXT, "
            + Contract.Alarms.LAST_EVENT_TIME + " TEXT, "
            + Contract.Alarms.LAST_EVENT_ID + " INTEGER, "
            + Contract.Alarms.LAST_EVENT_SEVERITY + " TEXT, "
            + Contract.Alarms.NODE_ID + " INTEGER, "
            + Contract.Alarms.NODE_LABEL + " TEXT, "
            + Contract.Alarms.SERVICE_TYPE_ID + " INTEGER, "
            + Contract.Alarms.SERVICE_TYPE_NAME + " TEXT, "
            + Contract.Alarms.LOG_MESSAGE + " TEXT"
            + ");";
    private static final String SQL_DROP_TABLE_ALARMS =
            "DROP TABLE IF EXISTS " + Contract.Tables.ALARMS;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database.");
        db.execSQL(SQL_CREATE_TABLE_NODES);
        db.execSQL(SQL_CREATE_TABLE_OUTAGES);
        db.execSQL(SQL_CREATE_TABLE_EVENTS);
        db.execSQL(SQL_CREATE_TABLE_ALARMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database.");
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

    public void wipe() {
        Log.d(TAG, "Wiping database.");
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            onUpgrade(db, 0, DATABASE_VERSION);
        }
    }

}
