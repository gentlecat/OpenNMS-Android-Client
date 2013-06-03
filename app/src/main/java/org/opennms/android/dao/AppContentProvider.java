package org.opennms.android.dao;

import android.content.ContentProvider;

public abstract class AppContentProvider extends ContentProvider {

    private static final String DB_NAME = "opennms";
    private static final int DB_VERSION = 1;
    protected DatabaseHelper db;

    @Override
    public boolean onCreate() {
        db = new DatabaseHelper(getContext(), DB_NAME, DB_VERSION);
        return db != null;
    }

}
