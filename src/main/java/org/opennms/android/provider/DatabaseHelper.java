package org.opennms.android.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.opennms.android.provider.Contract.Alarms;
import org.opennms.android.provider.Contract.Events;
import org.opennms.android.provider.Contract.Nodes;
import org.opennms.android.provider.Contract.Outages;
import org.opennms.android.provider.Contract.Tables;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";

    /** If you change the database schema, you must increment the database version! */
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "OpenNMS.db";
    /**
     * SQL
     */
    private static final String SQL_CREATE_TABLE_NODES =
            "CREATE TABLE IF NOT EXISTS " + Tables.NODES
            + " ("
            + Nodes._ID + " INTEGER PRIMARY KEY, "
            + Nodes.NAME + " TEXT, "
            + Nodes.CREATED_TIME + " TEXT, "
            + Nodes.LABEL_SOURCE + " TEXT, "
            + Nodes.CONTACT + " TEXT, "
            + Nodes.DESCRIPTION + " TEXT, "
            + Nodes.SYS_OBJECT_ID + " TEXT, "
            + Nodes.LOCATION + " TEXT"
            + ");";
    private static final String SQL_DROP_TABLE_NODES = "DROP TABLE IF EXISTS " + Tables.NODES;
    private static final String SQL_CREATE_TABLE_OUTAGES =
            "CREATE TABLE IF NOT EXISTS " + Tables.OUTAGES
            + " ("
            + Outages._ID + " INTEGER PRIMARY KEY, "
            + Outages.IP_ADDRESS + " TEXT, "
            + Outages.IP_INTERFACE_ID + " INTEGER, "
            + Outages.SERVICE_ID + " INTEGER, "
            + Outages.SERVICE_TYPE_NAME + " TEXT, "
            + Outages.SERVICE_TYPE_ID + " INTEGER, "
            + Outages.NODE_ID + " INTEGER, "
            + Outages.NODE_LABEL + " TEXT, "
            + Outages.SERVICE_LOST_TIME + " TEXT, "
            + Outages.SERVICE_LOST_EVENT_ID + " INTEGER, "
            + Outages.SERVICE_REGAINED_TIME + " TEXT, "
            + Outages.SERVICE_REGAINED_EVENT_ID + " INTEGER"
            + ");";
    private static final String SQL_DROP_TABLE_OUTAGES = "DROP TABLE IF EXISTS " + Tables.OUTAGES;
    private static final String SQL_CREATE_TABLE_EVENTS =
            "CREATE TABLE IF NOT EXISTS " + Tables.EVENTS
            + " ("
            + Events._ID + " INTEGER PRIMARY KEY, "
            + Events.SEVERITY + " TEXT, "
            + Events.LOG_MESSAGE + " TEXT, "
            + Events.DESCRIPTION + " TEXT, "
            + Events.HOST + " TEXT, "
            + Events.IP_ADDRESS + " TEXT, "
            + Events.NODE_ID + " INTEGER, "
            + Events.NODE_LABEL + " TEXT, "
            + Events.SERVICE_TYPE_ID + " INTEGER, "
            + Events.SERVICE_TYPE_NAME + " TEXT, "
            + Events.CREATE_TIME + " TEXT"
            + ");";
    private static final String SQL_DROP_TABLE_EVENTS = "DROP TABLE IF EXISTS " + Tables.EVENTS;
    private static final String SQL_CREATE_TABLE_ALARMS =
            "CREATE TABLE IF NOT EXISTS " + Tables.ALARMS
            + " ("
            + Alarms._ID + " INTEGER PRIMARY KEY, "
            + Alarms.SEVERITY + " TEXT, "
            + Alarms.ACK_USER + " TEXT, "
            + Alarms.ACK_TIME + " TEXT, "
            + Alarms.DESCRIPTION + " TEXT, "
            + Alarms.FIRST_EVENT_TIME + " TEXT, "
            + Alarms.LAST_EVENT_TIME + " TEXT, "
            + Alarms.LAST_EVENT_ID + " INTEGER, "
            + Alarms.LAST_EVENT_SEVERITY + " TEXT, "
            + Alarms.NODE_ID + " INTEGER, "
            + Alarms.NODE_LABEL + " TEXT, "
            + Alarms.SERVICE_TYPE_ID + " INTEGER, "
            + Alarms.SERVICE_TYPE_NAME + " TEXT, "
            + Alarms.LOG_MESSAGE + " TEXT"
            + ");";
    private static final String SQL_DROP_TABLE_ALARMS = "DROP TABLE IF EXISTS " + Tables.ALARMS;

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