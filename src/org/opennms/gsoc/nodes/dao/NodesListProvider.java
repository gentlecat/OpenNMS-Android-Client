package org.opennms.gsoc.nodes.dao;

import org.opennms.gsoc.dao.OnmsContentProvider;
import org.opennms.gsoc.dao.OnmsDatabaseHelper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class NodesListProvider extends OnmsContentProvider {

	private static final String AUTHORITY = "org.opennms.gsoc.nodes.dao.NodesListProvider";
	public static final int NODES = 100;
	public static final int NODE_ID = 110;
	public static final int NODE_LABEL = 120;

	private static final String NODES_BASE_PATH = "nodes";
	public static final Uri CONTENT_URI = Uri.parse("content://" + NodesListProvider.AUTHORITY
			+ "/" + NodesListProvider.NODES_BASE_PATH);

	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/opennms-node";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/opennms-node";

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		NodesListProvider.sURIMatcher.addURI(NodesListProvider.AUTHORITY, NodesListProvider.NODES_BASE_PATH, NodesListProvider.NODES);
		NodesListProvider.sURIMatcher.addURI(NodesListProvider.AUTHORITY, NodesListProvider.NODES_BASE_PATH + "/#", NodesListProvider.NODE_ID);
		NodesListProvider.sURIMatcher.addURI(NodesListProvider.AUTHORITY, NodesListProvider.NODES_BASE_PATH + "/label/*",NodesListProvider.NODE_LABEL);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = NodesListProvider.sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = this.mDB.getWritableDatabase();
		int rowsAffected = 0;
		switch (uriType) {
		case NODES:
			rowsAffected = sqlDB.delete(OnmsDatabaseHelper.TABLE_NODES,
					selection, selectionArgs);
			break;
		case NODE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsAffected = sqlDB.delete(OnmsDatabaseHelper.TABLE_NODES,
						OnmsDatabaseHelper.TABLE_NODES_ID + "=" + id, null);
			} else {
				rowsAffected = sqlDB.delete(OnmsDatabaseHelper.TABLE_NODES,
						selection + " and " + OnmsDatabaseHelper.TABLE_NODES_ID + "=" + id,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsAffected;
	}

	@Override
	public String getType(Uri uri) {
		int uriType = NodesListProvider.sURIMatcher.match(uri);
		switch (uriType) {
		case NODES:
			return NodesListProvider.CONTENT_TYPE;
		case NODE_ID:
			return NodesListProvider.CONTENT_ITEM_TYPE;
		default:
			return null;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = NodesListProvider.sURIMatcher.match(uri);
		if (uriType != NodesListProvider.NODES) {
			throw new IllegalArgumentException("Invalid URI for insert");
		}
		SQLiteDatabase sqlDB = this.mDB.getWritableDatabase();
		Uri newUri = null;
		try {
			long newID = sqlDB
					.insertWithOnConflict(OnmsDatabaseHelper.TABLE_NODES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			if (newID > 0) {
				newUri = ContentUris.withAppendedId(uri, newID);
				getContext().getContentResolver().notifyChange(uri, null);

			}
		} catch(SQLException e) {
			Log.e("NodesListProvider", "Failed to insert row into " + uri + e.getMessage());
		}
		return newUri;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = NodesListProvider.sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = this.mDB.getWritableDatabase();

		int rowsAffected = 0;

		switch (uriType) {
		case NODE_ID:
			String id = uri.getLastPathSegment();
			StringBuilder modSelection = new StringBuilder(OnmsDatabaseHelper.TABLE_NODES_ID
					+ "=" + id);

			if (!TextUtils.isEmpty(selection)) {
				modSelection.append(" AND " + selection);
			}

			rowsAffected = sqlDB.update(OnmsDatabaseHelper.TABLE_NODES,
					values, modSelection.toString(), null);
			break;
		case NODES:
			rowsAffected = sqlDB.update(OnmsDatabaseHelper.TABLE_NODES,
					values, selection, selectionArgs);
			break;
		default:
			break;
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsAffected;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(OnmsDatabaseHelper.TABLE_NODES);

		int uriType = NodesListProvider.sURIMatcher.match(uri);
		switch (uriType) {
		case NODE_ID:
			queryBuilder.appendWhere(OnmsDatabaseHelper.TABLE_NODES_ID + "="
					+ uri.getLastPathSegment());
			break;
		case NODE_LABEL:
			queryBuilder.appendWhere(OnmsDatabaseHelper.COL_LABEL + " like '%"
					+ uri.getLastPathSegment() + "%'");
			break;
		case NODES:
			break;
		default:
			break;
		}

		Cursor cursor = queryBuilder.query(this.mDB.getReadableDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

}
