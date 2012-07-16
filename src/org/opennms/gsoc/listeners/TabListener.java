package org.opennms.gsoc.listeners;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;

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
        mActivity = activity;
        mTag = tag;
        mClass = clz;
        mArgs = args;

        mFragment = mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
        if (mFragment != null && !mFragment.isDetached()) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
            ft.detach(mFragment);
            ft.commit();
        }
    }

    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (mFragment == null) {
            mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
            ft.add(android.R.id.content, mFragment, mTag);
        } else {
            ft.attach(mFragment);
        }
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            FragmentManager man = mActivity.getSupportFragmentManager();
            if(man.getBackStackEntryCount()>0) //this check is required to prevent null point exceptions when clicking off of a tab with no history
                man.popBackStack(man.getBackStackEntryAt(0).getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE); //this pops the stack back to index 0 so you can then detach and then later attach your initial fragment
            ft.detach(mFragment);
        }
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
    }
}
