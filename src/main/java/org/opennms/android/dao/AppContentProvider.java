package org.opennms.android.dao;

import android.content.ContentProvider;
import android.content.Context;

public abstract class AppContentProvider extends ContentProvider {

    protected DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        dbHelper = new DatabaseHelper(context);
        return true;
    }

}
