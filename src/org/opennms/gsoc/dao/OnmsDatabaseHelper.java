package org.opennms.gsoc.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OnmsDatabaseHelper extends SQLiteOpenHelper{
	private static final String DB_NAME = "opennms";
	private static final int DB_VERSION = 1;
	private static final String DEBUG_TAG = "OnmsDatabaseHelper";

	public static final String TABLE_OUTAGES = "outages";
	public static final String TABLE_OUTAGES_ID = "_id";
	public static final String COL_OUTAGE_ID = "outage_id";
	public static final String COL_IP_ADDRESS = "ip_address";
	public static final String COL_IF_REGAINED_SERVICE = "if_regained_service";
	public static final String COL_SERVICE_TYPE_NAME = "service_type_name";
	public static final String COL_IF_LOST_SERVICE = "if_lost_service";

	public static final String TABLE_NODES = "nodes";
	public static final String TABLE_NODES_ID = "_id";
	public static final String COL_NODE_ID = "node_id";
	public static final String COL_TYPE = "type";
	public static final String COL_LABEL = "label";
	public static final String COL_CREATED_TIME = "created_time";
	public static final String COL_LABEL_SOURCE = "label_source";
	public static final String COL_SYS_CONTACT= "sys_contact";

	public static final String TABLE_ALARMS = "alarms";
	public static final String TABLE_ALARMS_ID = "_id";
	public static final String COL_ALARM_ID = "alarm_id";
	public static final String COL_SEVERITY = "severity";
	public static final String COL_DESCRIPTION = "description";
	public static final String COL_LOG_MESSAGE = "log_message";

	private static final String CREATE_TABLE_NODES = "create table if not exists " + OnmsDatabaseHelper.TABLE_NODES
			+ " (" + OnmsDatabaseHelper.TABLE_NODES_ID + " integer primary key autoincrement, " + OnmsDatabaseHelper.COL_NODE_ID + " integer unique, " + OnmsDatabaseHelper.COL_TYPE
			+ " text not null, " + OnmsDatabaseHelper.COL_LABEL + " text not null, " + OnmsDatabaseHelper.COL_CREATED_TIME + " text, " + OnmsDatabaseHelper.COL_LABEL_SOURCE + " text, " + OnmsDatabaseHelper.COL_SYS_CONTACT + " text);";

	private static final String CREATE_TABLE_OUTAGES = "create table if not exists " + OnmsDatabaseHelper.TABLE_OUTAGES
			+ " (" + OnmsDatabaseHelper.TABLE_OUTAGES_ID + " integer primary key autoincrement, " + OnmsDatabaseHelper.COL_OUTAGE_ID + " integer unique, " + OnmsDatabaseHelper.COL_IP_ADDRESS
			+ " text, " + OnmsDatabaseHelper.COL_IF_REGAINED_SERVICE + " text, " + OnmsDatabaseHelper.COL_SERVICE_TYPE_NAME + " text, " + OnmsDatabaseHelper.COL_IF_LOST_SERVICE + " text);";

	private static final String CREATE_TABLE_ALARMS = "create table if not exists " + OnmsDatabaseHelper.TABLE_ALARMS
			+ " (" + OnmsDatabaseHelper.TABLE_ALARMS_ID + " integer primary key autoincrement, " + OnmsDatabaseHelper.COL_ALARM_ID + " integer unique, " + OnmsDatabaseHelper.COL_SEVERITY
			+ " text, " + OnmsDatabaseHelper.COL_DESCRIPTION + " text, " + OnmsDatabaseHelper.COL_LOG_MESSAGE + " text);";

	public OnmsDatabaseHelper(Context context) {
		super(context, OnmsDatabaseHelper.DB_NAME, null, OnmsDatabaseHelper.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(OnmsDatabaseHelper.CREATE_TABLE_OUTAGES);
		db.execSQL(OnmsDatabaseHelper.CREATE_TABLE_NODES);
		db.execSQL(OnmsDatabaseHelper.CREATE_TABLE_ALARMS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(OnmsDatabaseHelper.DEBUG_TAG, "Upgrading database. Existing contents will be lost. ["
				+ oldVersion + "]->[" + newVersion + "]");
		db.execSQL("DROP TABLE IF EXISTS " + OnmsDatabaseHelper.TABLE_OUTAGES);
		db.execSQL("DROP TABLE IF EXISTS " + OnmsDatabaseHelper.TABLE_NODES);
		db.execSQL("DROP TABLE IF EXISTS " + OnmsDatabaseHelper.TABLE_ALARMS);
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		db.execSQL(OnmsDatabaseHelper.CREATE_TABLE_OUTAGES);
		db.execSQL(OnmsDatabaseHelper.CREATE_TABLE_NODES);
		db.execSQL(OnmsDatabaseHelper.CREATE_TABLE_ALARMS);
	}
}