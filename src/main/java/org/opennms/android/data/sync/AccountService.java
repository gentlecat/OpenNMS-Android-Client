package org.opennms.android.data.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class AccountService extends Service {

  private static final String TAG = "AccountService";
  public static final String ACCOUNT_NAME = "Alarms sync";
  private static final String ACCOUNT_TYPE = "org.opennms.android.data.sync.AlarmsSyncAdapter";
  private Authenticator authenticator;

  /**
   * Obtain a handle to the {@link android.accounts.Account} used for sync in this application.
   *
   * @return Handle to application's account (not guaranteed to resolve unless createSyncAccount()
   * has been called)
   */
  public static Account getAccount() {
    // Note: Normally the account name is set to the user's identity (username or email
    // address). However, since we aren't actually using any user accounts, it makes more sense
    // to use a generic string in this case.
    //
    // This string should *not* be localized. If the user switches locale, we would not be
    // able to locate the old account, and may erroneously register multiple accounts.
    final String accountName = ACCOUNT_NAME;
    return new Account(accountName, ACCOUNT_TYPE);
  }

  @Override
  public void onCreate() {
    Log.i(TAG, "Service created");
    authenticator = new Authenticator(this);
  }

  @Override
  public void onDestroy() {
    Log.i(TAG, "Service destroyed");
  }

  @Override
  public IBinder onBind(Intent intent) {
    return authenticator.getIBinder();
  }

  public class Authenticator extends AbstractAccountAuthenticator {

    public Authenticator(Context context) {
      super(context);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                 String s) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse,
                             String s, String s2, String[] strings, Bundle bundle)
        throws NetworkErrorException {
      return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                     Account account, Bundle bundle)
        throws NetworkErrorException {
      return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse,
                               Account account, String s, Bundle bundle)
        throws NetworkErrorException {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthTokenLabel(String s) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                    Account account, String s, Bundle bundle)
        throws NetworkErrorException {
      throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse,
                              Account account, String[] strings)
        throws NetworkErrorException {
      throw new UnsupportedOperationException();
    }
  }

}

