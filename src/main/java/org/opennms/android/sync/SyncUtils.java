package org.opennms.android.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import org.opennms.android.data.storage.Contract;

/**
 * Static helper methods for working with the sync framework.
 */
public class SyncUtils {

    public static final String TAG = "SyncUtils";
    public static final String CONTENT_AUTHORITY = Contract.CONTENT_AUTHORITY;

    /**
     * Create an entry for this application in the system account list, if it isn't already there.
     *
     * @param context Context
     */
    public static void createSyncAccount(Context context) {
        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = AccountService.getAccount();
        AccountManager accountManager = (AccountManager) context
                .getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
        }
    }

    public static void setSyncAlarmsPeriodically(boolean sync, Account account, long frequencySec) {
        if (sync) {
            Bundle bundle = new Bundle();

            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(account, CONTENT_AUTHORITY, bundle, frequencySec);
        } else {
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, false);
            ContentResolver.removePeriodicSync(account, CONTENT_AUTHORITY, new Bundle());
        }
    }

}
