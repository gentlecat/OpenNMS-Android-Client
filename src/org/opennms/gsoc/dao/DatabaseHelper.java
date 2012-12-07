package org.opennms.gsoc.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "opennms";
	private static final int DB_VERSION = 1;

    // Outages table
	public static final String TABLE_OUTAGES = "outages";
	public static final String TABLE_OUTAGES_ID = "_id";
	public static final String COL_OUTAGE_ID = "outage_id";
	public static final String COL_IP_ADDRESS = "ip_address";
	public static final String COL_IF_REGAINED_SERVICE = "if_regained_service";
	public static final String COL_SERVICE_TYPE_NAME = "service_type_name";
	public static final String COL_IF_LOST_SERVICE = "if_lost_service";

    // Nodes table
	public static final String TABLE_NODES = "nodes";
	public static final String TABLE_NODES_ID = "_id";
	public static final String COL_NODE_ID = "node_id";
	public static final String COL_TYPE = "type";
	public static final String COL_LABEL = "label";
	public static final String COL_CREATED_TIME = "created_time";
	public static final String COL_LABEL_SOURCE = "label_source";
	public static final String COL_SYS_CONTACT= "sys_contact";

    // Alarms table
	public static final String TABLE_ALARMS = "alarms";
	public static final String TABLE_ALARMS_ID = "_id";
	public static final String COL_ALARM_ID = "alarm_id";
	public static final String COL_SEVERITY = "severity";
	public static final String COL_DESCRIPTION = "description";
	public static final String COL_LOG_MESSAGE = "log_message";

    /*
        Queries
     */
	private static final String CREATE_TABLE_NODES = "create table if not exists " + DatabaseHelper.TABLE_NODES
            + " (" + DatabaseHelper.TABLE_NODES_ID + " integer primary key autoincrement, "
            + DatabaseHelper.COL_NODE_ID + " integer unique, " + DatabaseHelper.COL_TYPE + " text not null, "
            + DatabaseHelper.COL_LABEL + " text not null, " + DatabaseHelper.COL_CREATED_TIME + " text, "
            + DatabaseHelper.COL_LABEL_SOURCE + " text, " + DatabaseHelper.COL_SYS_CONTACT + " text);";
	private static final String CREATE_TABLE_OUTAGES = "create table if not exists " + DatabaseHelper.TABLE_OUTAGES
			+ " (" + DatabaseHelper.TABLE_OUTAGES_ID + " integer primary key autoincrement, "
            + DatabaseHelper.COL_OUTAGE_ID + " integer unique, " + DatabaseHelper.COL_IP_ADDRESS + " text, "
            + DatabaseHelper.COL_IF_REGAINED_SERVICE + " text, " + DatabaseHelper.COL_SERVICE_TYPE_NAME + " text, "
            + DatabaseHelper.COL_IF_LOST_SERVICE + " text);";
   	private static final String CREATE_TABLE_ALARMS = "create table if not exists " + DatabaseHelper.TABLE_ALARMS
			+ " (" + DatabaseHelper.TABLE_ALARMS_ID + " integer primary key autoincrement, "
            + DatabaseHelper.COL_ALARM_ID + " integer unique, " + DatabaseHelper.COL_SEVERITY + " text, "
            + DatabaseHelper.COL_DESCRIPTION + " text, " + DatabaseHelper.COL_LOG_MESSAGE + " text);";

	public DatabaseHelper(Context context) {
		super(context, DatabaseHelper.DB_NAME, null, DatabaseHelper.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DatabaseHelper.CREATE_TABLE_OUTAGES);
		db.execSQL(DatabaseHelper.CREATE_TABLE_NODES);
		db.execSQL(DatabaseHelper.CREATE_TABLE_ALARMS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_OUTAGES);
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_NODES);
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_ALARMS);
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		db.execSQL(DatabaseHelper.CREATE_TABLE_OUTAGES);
		db.execSQL(DatabaseHelper.CREATE_TABLE_NODES);
		db.execSQL(DatabaseHelper.CREATE_TABLE_ALARMS);
	}

}