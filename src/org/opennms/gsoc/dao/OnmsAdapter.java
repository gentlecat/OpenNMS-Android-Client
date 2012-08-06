package org.opennms.gsoc.dao;

import java.util.List;

import org.opennms.gsoc.model.OnmsNode;
import org.opennms.gsoc.nodes.dao.NodesListProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class OnmsAdapter {

	private Context ctx ;
	private OnmsDatabaseHelper dbHelper ;
	private SQLiteDatabase db ;

	/**
	 * Default constructor
	 * @param context The application's {@link Context}
	 */
	public OnmsAdapter(Context context) {
		this.ctx = context ;
	}

	/**
	 * Opens the database connection and returns an instance of the database adapter.
	 * @return The {@link ONMSDataAdapter} which allow manipulation of the database
	 * @throws SQLException If there is an error connecting to the database
	 */
	public OnmsAdapter open() throws SQLException {
		this.dbHelper = new OnmsDatabaseHelper(this.ctx) ;
		this.db = this.dbHelper.getWritableDatabase() ;
		return this ;
	}

	/**
	 * Closes the database connection
	 */
	public void close() {
		this.dbHelper.close() ;
	}

	public void addNodes(List<OnmsNode> nodes) {
		for(OnmsNode node : nodes) {
			ContentValues tutorialData = new ContentValues();
			tutorialData.put(OnmsDatabaseHelper.COL_NODE_ID, node.getId());
			tutorialData.put(OnmsDatabaseHelper.COL_TYPE, node.getType());
			tutorialData.put(OnmsDatabaseHelper.COL_LABEL, node.getLabel());
			this.db.insertWithOnConflict(NodesListProvider.CONTENT_URI.getLastPathSegment(), null, tutorialData, SQLiteDatabase.CONFLICT_REPLACE);
		}
	}
}
