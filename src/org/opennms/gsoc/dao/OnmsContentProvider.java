package org.opennms.gsoc.dao;

import android.content.ContentProvider;

public abstract class OnmsContentProvider extends ContentProvider{
	protected OnmsDatabaseHelper mDB;

	public void reset() {
		onCreate();
	}

	@Override
	public boolean onCreate() {
		this.mDB = new OnmsDatabaseHelper(getContext());
		return (this.mDB != null);
	}

}
