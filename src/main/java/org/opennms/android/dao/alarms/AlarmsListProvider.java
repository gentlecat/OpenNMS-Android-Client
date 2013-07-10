package org.opennms.android.dao.alarms;

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

public class AlarmsListProvider extends AppContentProvider {

    public static final int ALARMS = 100;
    public static final int ALARM_ID = 110;
    public static final int ALARM_SEVERITY = 120;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AlarmsListProvider.AUTHORITY
            + "/" + AlarmsListProvider.ALARMS_BASE_PATH);
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/opennms-node";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/opennms-node";
    private static final String AUTHORITY = "org.opennms.android.dao.alarms.AlarmsListProvider";
    private static final String ALARMS_BASE_PATH = "alarms";
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, ALARMS_BASE_PATH, ALARMS);
        sURIMatcher.addURI(AUTHORITY, ALARMS_BASE_PATH + "/#", ALARM_ID);
        sURIMatcher.addURI(AUTHORITY, ALARMS_BASE_PATH + "/severity/*", ALARM_SEVERITY);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DatabaseHelper.Tables.ALARMS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case ALARM_ID:
                queryBuilder.appendWhere(BaseColumns._ID + "=" + uri.getLastPathSegment());
                break;
            case ALARM_SEVERITY:
                queryBuilder.appendWhere(Columns.AlarmColumns.SEVERITY + " like '%" + uri.getLastPathSegment() + "%'");
                break;
            case ALARMS:
                break;
            default:
                break;
        }

        Cursor cursor = queryBuilder.query(this.db.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        if (uriType != ALARMS) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }
        SQLiteDatabase sqlDB = this.db.getWritableDatabase();
        Uri newUri = null;
        try {
            long newID = sqlDB
                    .insertWithOnConflict(DatabaseHelper.Tables.ALARMS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (newID > 0) {
                newUri = ContentUris.withAppendedId(uri, newID);
                getContext().getContentResolver().notifyChange(uri, null);

            }
        } catch (SQLException e) {
            Log.e("AlarmsListProvider", "Failed to insert row into " + uri + e.getMessage());
        }
        return newUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = this.db.getWritableDatabase();

        int rowsAffected = 0;

        switch (uriType) {
            case ALARM_ID:
                String id = uri.getLastPathSegment();
                StringBuilder modSelection = new StringBuilder(BaseColumns._ID + "=" + id);

                if (!TextUtils.isEmpty(selection)) {
                    modSelection.append(" AND " + selection);
                }

                rowsAffected = sqlDB.update(DatabaseHelper.Tables.ALARMS, values, modSelection.toString(), null);
                break;
            case ALARMS:
                rowsAffected = sqlDB.update(DatabaseHelper.Tables.ALARMS, values, selection, selectionArgs);
                break;
            default:
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = this.db.getWritableDatabase();
        int rowsAffected = 0;
        switch (uriType) {
            case ALARMS:
                rowsAffected = sqlDB.delete(DatabaseHelper.Tables.ALARMS, selection, selectionArgs);
                break;
            case ALARM_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = sqlDB.delete(DatabaseHelper.Tables.ALARMS,
                            BaseColumns._ID + "=" + id,
                            null);
                } else {
                    rowsAffected = sqlDB.delete(DatabaseHelper.Tables.ALARMS,
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
            case ALARMS:
                return CONTENT_TYPE;
            case ALARM_ID:
                return CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

}
