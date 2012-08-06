package org.opennms.gsoc.listeners;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class TabListener<T extends Fragment> implements ActionBar.TabListener {
	private final SherlockFragmentActivity mActivity;
	private final String mTag;
	private final Class<T> mClass;
	private final Bundle mArgs;
	private Fragment mFragment;

	public TabListener(SherlockFragmentActivity activity, String tag, Class<T> clz) {
		this(activity, tag, clz, null);
	}

	public TabListener(SherlockFragmentActivity activity, String tag, Class<T> clz, Bundle args) {
		this.mActivity = activity;
		this.mTag = tag;
		this.mClass = clz;
		this.mArgs = args;

		this.mFragment = this.mActivity.getSupportFragmentManager().findFragmentByTag(this.mTag);
		if (this.mFragment != null && !this.mFragment.isDetached()) {
			FragmentTransaction ft = this.mActivity.getSupportFragmentManager().beginTransaction();
			ft.detach(this.mFragment);
			ft.commit();
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (this.mFragment == null) {
			this.mFragment = Fragment.instantiate(this.mActivity, this.mClass.getName(), this.mArgs);
			ft.add(android.R.id.content, this.mFragment, this.mTag);
		} else {
			ft.attach(this.mFragment);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (this.mFragment != null) {
			FragmentManager man = this.mActivity.getSupportFragmentManager();
			if(man.getBackStackEntryCount()>0)
			{
				man.popBackStack(man.getBackStackEntryAt(0).getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE); //this pops the stack back to index 0 so you can then detach and then later attach your initial fragment
			}
			ft.detach(this.mFragment);
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		Toast.makeText(this.mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
	}
}
