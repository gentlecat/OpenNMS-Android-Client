package org.opennms.android.outages.dao;

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
import org.opennms.android.dao.AppContentProvider;
import org.opennms.android.dao.DatabaseHelper;

public class OutagesListProvider extends AppContentProvider {

    public static final int OUTAGES = 200;
    public static final int OUTAGE_ID = 210;
    public static final Uri CONTENT_URI = Uri.parse("content://" + OutagesListProvider.AUTHORITY
            + "/" + OutagesListProvider.OUTAGES_BASE_PATH);
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/opennms-outage";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/opennms-outage";
    private static final String AUTHORITY = "org.opennms.android.outages.dao.OutagesListProvider";
    private static final int OUTAGE_IP_ADDRESS = 220;
    private static final String OUTAGES_BASE_PATH = "outages";
    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        OutagesListProvider.sURIMatcher.addURI(OutagesListProvider.AUTHORITY, OutagesListProvider.OUTAGES_BASE_PATH, OutagesListProvider.OUTAGES);
        OutagesListProvider.sURIMatcher.addURI(OutagesListProvider.AUTHORITY, OutagesListProvider.OUTAGES_BASE_PATH + "/#", OutagesListProvider.OUTAGE_ID);
        OutagesListProvider.sURIMatcher.addURI(OutagesListProvider.AUTHORITY, OutagesListProvider.OUTAGES_BASE_PATH + "/ipaddress/*", OutagesListProvider.OUTAGE_IP_ADDRESS);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = OutagesListProvider.sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = this.db.getWritableDatabase();
        int rowsAffected = 0;
        switch (uriType) {
            case OUTAGES:
                rowsAffected = sqlDB.delete(DatabaseHelper.TABLE_OUTAGES,
                        selection, selectionArgs);
                break;
            case OUTAGE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = sqlDB.delete(DatabaseHelper.TABLE_OUTAGES,
                            DatabaseHelper.TABLE_OUTAGES_ID + "=" + id, null);
                } else {
                    rowsAffected = sqlDB.delete(DatabaseHelper.TABLE_OUTAGES,
                            selection + " and " + DatabaseHelper.TABLE_OUTAGES_ID + "=" + id,
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
        int uriType = OutagesListProvider.sURIMatcher.match(uri);
        switch (uriType) {
            case OUTAGES:
                return OutagesListProvider.CONTENT_TYPE;
            case OUTAGE_ID:
                return OutagesListProvider.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = OutagesListProvider.sURIMatcher.match(uri);
        if (uriType != OutagesListProvider.OUTAGES) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }
        SQLiteDatabase sqlDB = this.db.getWritableDatabase();
        Uri newUri = null;
        try {
            long newID = sqlDB
                    .insertWithOnConflict(DatabaseHelper.TABLE_OUTAGES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (newID > 0) {
                newUri = ContentUris.withAppendedId(uri, newID);
                getContext().getContentResolver().notifyChange(uri, null);

            }
        } catch (SQLException e) {
            Log.e("OutagesListProvider", "Failed to insert row into " + uri + e.getMessage());
        }
        return newUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = OutagesListProvider.sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = this.db.getWritableDatabase();

        int rowsAffected = 0;

        switch (uriType) {
            case OUTAGE_ID:
                String id = uri.getLastPathSegment();
                StringBuilder modSelection = new StringBuilder(DatabaseHelper.TABLE_OUTAGES_ID
                        + "=" + id);

                if (!TextUtils.isEmpty(selection)) {
                    modSelection.append(" AND " + selection);
                }

                rowsAffected = sqlDB.update(DatabaseHelper.TABLE_OUTAGES,
                        values, modSelection.toString(), null);
                break;
            case OUTAGES:
                rowsAffected = sqlDB.update(DatabaseHelper.TABLE_OUTAGES,
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
        queryBuilder.setTables(DatabaseHelper.TABLE_OUTAGES);

        int uriType = OutagesListProvider.sURIMatcher.match(uri);
        switch (uriType) {
            case OUTAGE_ID:
                queryBuilder.appendWhere(DatabaseHelper.TABLE_OUTAGES_ID + "="
                        + uri.getLastPathSegment());
                break;
            case OUTAGE_IP_ADDRESS:
                queryBuilder.appendWhere(DatabaseHelper.COL_IP_ADDRESS + " like '%"
                        + uri.getLastPathSegment() + "%'");
                break;
            case OUTAGES:
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

