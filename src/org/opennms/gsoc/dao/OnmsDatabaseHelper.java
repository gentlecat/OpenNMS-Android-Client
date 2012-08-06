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
	public static final String COL_IP_ADDRESS = "ipAddress";

	public static final String TABLE_NODES = "nodes";
	public static final String TABLE_NODES_ID = "_id";
	public static final String COL_NODE_ID = "node_id";
	public static final String COL_TYPE = "type";
	public static final String COL_LABEL = "label";

	private static final String CREATE_TABLE_NODES = "create table if not exists " + OnmsDatabaseHelper.TABLE_NODES
			+ " (" + OnmsDatabaseHelper.TABLE_NODES_ID + " integer primary key autoincrement, " + OnmsDatabaseHelper.COL_NODE_ID + " integer unique, " + OnmsDatabaseHelper.COL_TYPE
			+ " text not null, " + OnmsDatabaseHelper.COL_LABEL + " text not null);";


	private static final String CREATE_TABLE_OUTAGES = "create table if not exists " + OnmsDatabaseHelper.TABLE_OUTAGES
			+ " (" + OnmsDatabaseHelper.TABLE_OUTAGES_ID + " integer primary key autoincrement, " + OnmsDatabaseHelper.COL_OUTAGE_ID + " integer unique, " + OnmsDatabaseHelper.COL_IP_ADDRESS
			+ " text);";


	public OnmsDatabaseHelper(Context context) {
		super(context, OnmsDatabaseHelper.DB_NAME, null, OnmsDatabaseHelper.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(OnmsDatabaseHelper.CREATE_TABLE_OUTAGES);
		db.execSQL(OnmsDatabaseHelper.CREATE_TABLE_NODES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(OnmsDatabaseHelper.DEBUG_TAG, "Upgrading database. Existing contents will be lost. ["
				+ oldVersion + "]->[" + newVersion + "]");
		db.execSQL("DROP TABLE IF EXISTS " + OnmsDatabaseHelper.TABLE_OUTAGES);
		db.execSQL("DROP TABLE IF EXISTS " + OnmsDatabaseHelper.TABLE_NODES);
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		db.execSQL(OnmsDatabaseHelper.CREATE_TABLE_OUTAGES);
		db.execSQL(OnmsDatabaseHelper.CREATE_TABLE_NODES);
	}
}