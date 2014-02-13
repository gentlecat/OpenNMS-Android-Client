package org.opennms.android.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service that returns an IBinder for the sync adapter class, allowing the sync adapter framework
 * to call onPerformSync().
 */
public class AlarmsSyncService extends Service {

  // Object to use as a thread-safe lock
  private static final Object syncAdapterLock = new Object();
  // Storage for an instance of the sync adapter
  private static AlarmsSyncAdapter syncAdapter = null;

  /*
   * Instantiate the sync adapter object.
   */
  @Override
  public void onCreate() {
    /*
     * Create the sync adapter as a singleton.
     * Set the sync adapter as syncable
     * Disallow parallel syncs
     */
    synchronized (syncAdapterLock) {
      if (syncAdapter == null) {
        syncAdapter = new AlarmsSyncAdapter(getApplicationContext(), true);
      }
    }
  }

  /**
   * Return an object that allows the system to invoke the sync adapter.
   */
  @Override
  public IBinder onBind(Intent intent) {
    /*
     * Get the object that allows external processes
     * to call onPerformSync(). The object is created
     * in the base class code when the AlarmsSyncAdapter
     * constructors call super()
     */
    return syncAdapter.getSyncAdapterBinder();
  }

}