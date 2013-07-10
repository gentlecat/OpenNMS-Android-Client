package org.opennms.android.dao.events;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import org.opennms.android.dao.AppContentProvider;
import org.opennms.android.dao.Columns;
import org.opennms.android.dao.DatabaseHelper;

public class EventsListProvider extends AppContentProvider {

    public static final int EVENT = 100;
    public static final int EVENT_ID = 110;
    public static final int EVENT_SEVERITY = 120;
    public static final Uri CONTENT_URI = Uri.parse("content://" + EventsListProvider.AUTHORITY
            + "/" + EventsListProvider.EVENTS_BASE_PATH);
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/opennms-node";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/opennms-node";
    private static final String AUTHORITY = "org.opennms.android.dao.events.EventsListProvider";
    private static final String EVENTS_BASE_PATH = "events";
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, EVENTS_BASE_PATH, EVENT);
        sURIMatcher.addURI(AUTHORITY, EVENTS_BASE_PATH + "/#", EVENT_ID);
        sURIMatcher.addURI(AUTHORITY, EVENTS_BASE_PATH + "/severity/*", EVENT_SEVERITY);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = this.db.getWritableDatabase();
        int rowsAffected = 0;
        switch (uriType) {
            case EVENT:
                rowsAffected = sqlDB.delete(DatabaseHelper.Tables.EVENTS, selection, selectionArgs);
                break;
            case EVENT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = sqlDB.delete(DatabaseHelper.Tables.EVENTS,
                            BaseColumns._ID + "=" + id,
                            null);
                } else {
                    rowsAffected = sqlDB.delete(DatabaseHelper.Tables.EVENTS,
                            selection + " and " + BaseColumns._ID + "=" + id,
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
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case EVENT:
                return CONTENT_TYPE;
            case EVENT_ID:
                return CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        if (uriType != EVENT) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }
        SQLiteDatabase sqlDB = this.db.getWritableDatabase();
        Uri newUri = null;
        try {
            long newID = sqlDB
                    .insertWithOnConflict(DatabaseHelper.Tables.EVENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (newID > 0) {
                newUri = ContentUris.withAppendedId(uri, newID);
                getContext().getContentResolver().notifyChange(uri, null);

            }
        } catch (SQLException e) {
            Log.e("EventsListProvider", "Failed to insert row into " + uri + e.getMessage());
        }
        return newUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = this.db.getWritableDatabase();

        int rowsAffected = 0;

        switch (uriType) {
            case EVENT_ID:
                String id = uri.getLastPathSegment();
                StringBuilder modSelection = new StringBuilder(BaseColumns._ID + "=" + id);
                if (!TextUtils.isEmpty(selection)) {
                    modSelection.append(" AND ").append(selection);
                }
                rowsAffected = sqlDB.update(DatabaseHelper.Tables.EVENTS, values, modSelection.toString(), null);
                break;
            case EVENT:
                rowsAffected = sqlDB.update(DatabaseHelper.Tables.EVENTS, values, selection, selectionArgs);
                break;
            default:
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DatabaseHelper.Tables.EVENTS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case EVENT_ID:
                queryBuilder.appendWhere(BaseColumns._ID + "=" + uri.getLastPathSegment());
                break;
            case EVENT_SEVERITY:
                queryBuilder.appendWhere(Columns.EventColumns.SEVERITY + " like '%" + uri.getLastPathSegment() + "%'");
                break;
            case EVENT:
                break;
            default:
                break;
        }

        Cursor cursor = queryBuilder.query(this.db.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

}
