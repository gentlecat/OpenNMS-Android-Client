package org.opennms.gsoc.dao;

import android.content.ContentProvider;

public abstract class AppContentProvider extends ContentProvider {

    protected DatabaseHelper db;

    public void reset() {
        onCreate();
    }

    @Override
    public boolean onCreate() {
        db = new DatabaseHelper(getContext());
        return (db != null);
    }

}
