package org.opennms.android.ui.alarms;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.data.ContentValuesGenerator;
import org.opennms.android.data.api.model.Alarm;
import org.opennms.android.data.storage.Contract;
import org.opennms.android.ui.ActivityUtils;
import org.opennms.android.ui.BaseActivity;
import org.opennms.android.ui.DetailsFragment;

import retrofit.RetrofitError;

public class AlarmDetailsFragment extends DetailsFragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final String TAG = "AlarmDetailsFragment";
  private static final int LOADER_ID = 0x1;
  private long alarmId;
  private LoaderManager loaderManager;
  private Menu menu;
  private MenuItem ackMenuItem;
  private MenuItem unackMenuItem;
  private Boolean isAcked = null;

  // Do not remove!
  public AlarmDetailsFragment() {
  }

  public AlarmDetailsFragment(long alarmId) {
    this.alarmId = alarmId;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle data) {
    return new CursorLoader(getActivity(),
                            Uri.withAppendedPath(Contract.Alarms.CONTENT_URI,
                                                 String.valueOf(alarmId)),
                            null, null, null, null);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    if (!isAdded()) {
      return;
    }
    if (cursor != null && cursor.moveToFirst()) {
      updateContent(cursor);
    } else {
      showErrorMessage();
    }
    if (cursor != null) {
      cursor.close();
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    loaderManager = getLoaderManager();
  }

  @Override
  public void onStart() {
    super.onStart();
    loaderManager.restartLoader(LOADER_ID, null, this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.details_loading, container, false);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    this.menu = menu;
    inflater.inflate(R.menu.alarm, menu);
    ackMenuItem = menu.findItem(R.id.menu_ack_alarm);
    unackMenuItem = menu.findItem(R.id.menu_unack_alarm);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    boolean isDrawerOpen = ((BaseActivity) getActivity()).isDrawerOpen();
    if (isDrawerOpen) {
      ackMenuItem.setVisible(false);
      unackMenuItem.setVisible(false);
    } else {
      // TODO: Fix (one of the items is displayed when ack/unack process is active, that shouldn't happen)
      // TODO: Fix (if update is attempted before onCreateOptionsMenu is called)
      if (isAcked != null) {
        updateMenu(isAcked);
      }
    }
  }

  private void updateMenu(boolean acknowledged) {
    if (ackMenuItem != null) {
      ackMenuItem.setVisible(!acknowledged);
    }
    if (unackMenuItem != null) {
      unackMenuItem.setVisible(acknowledged);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_ack_alarm:
        acknowledge();
        return true;
      case R.id.menu_unack_alarm:
        unacknowledge();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void acknowledge() {
    if (Utils.isOnline(getActivity())) {
      new AcknowledgementTask(this).execute();
    } else {
      Toast.makeText(getActivity(), getString(R.string.alarm_ack_fail_offline),
                     Toast.LENGTH_LONG).show();
    }
  }

  public void unacknowledge() {
    if (Utils.isOnline(getActivity())) {
      new UnacknowledgementTask(this).execute();
    } else {
      Toast.makeText(getActivity(), getString(R.string.alarm_unack_fail_offline),
                     Toast.LENGTH_LONG).show();
    }
  }

  public void updateContent(Cursor cursor) {
    if (!cursor.moveToFirst()) {
      return;
    }

    RelativeLayout detailsContainer =
        (RelativeLayout) getActivity().findViewById(R.id.details_container);
    if (detailsContainer == null) {
      return;
    }
    detailsContainer.removeAllViews();
    LayoutInflater inflater = (LayoutInflater) getActivity()
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.alarm_details, detailsContainer);

    LinearLayout detailsLayout =
        (LinearLayout) getActivity().findViewById(R.id.alarm_details);

    // Alarm ID
    int id = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Alarms._ID));
    TextView idView = (TextView) getActivity().findViewById(R.id.alarm_id);
    idView.setText(getString(R.string.alarm_details_id) + id);

    // Severity
    String severity =
        cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.SEVERITY));
    TextView severityView = (TextView) getActivity().findViewById(R.id.alarm_severity);
    severityView.setText(String.valueOf(severity));
    LinearLayout severityRow =
        (LinearLayout) getActivity().findViewById(R.id.alarm_severity_row);
    if (severity.equals("CLEARED")) {
      severityRow.setBackgroundColor(getResources().getColor(R.color.severity_cleared));
    } else if (severity.equals("MINOR")) {
      severityRow.setBackgroundColor(getResources().getColor(R.color.severity_minor));
    } else if (severity.equals("NORMAL")) {
      severityRow.setBackgroundColor(getResources().getColor(R.color.severity_normal));
    } else if (severity.equals("INDETERMINATE")) {
      severityRow.setBackgroundColor(getResources().getColor(R.color.severity_minor));
    } else if (severity.equals("WARNING")) {
      severityRow.setBackgroundColor(getResources().getColor(R.color.severity_warning));
    } else if (severity.equals("MAJOR")) {
      severityRow.setBackgroundColor(getResources().getColor(R.color.severity_major));
    } else if (severity.equals("CRITICAL")) {
      severityRow.setBackgroundColor(getResources().getColor(R.color.severity_critical));
    }

    // Acknowledgement info
    String ackTime =
        cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.ACK_TIME));
    String ackUser =
        cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.ACK_USER));
    TextView ackStatus = (TextView) getActivity().findViewById(R.id.alarm_ack_status);
    TextView ackMessage = (TextView) getActivity().findViewById(R.id.alarm_ack_message);
    if (ackTime != null) {
      ackStatus.setText(getString(R.string.alarm_details_acked));
      ackMessage.setText(ackTime
                         + " " + getString(R.string.alarm_details_acked_by) + " "
                         + ackUser);
    } else {
      ackStatus.setText(getString(R.string.alarm_details_not_acked));
      ackMessage.setText("");
    }

    // Description
    String desc =
        cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.DESCRIPTION));
    TextView descView = (TextView) getActivity().findViewById(R.id.alarm_description);
    descView.setText(Html.fromHtml(desc));

    // Log message
    String logMessage =
        cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.LOG_MESSAGE));
    TextView logMessageView = (TextView) getActivity().findViewById(R.id.alarm_log_message);
    logMessageView.setText(Html.fromHtml(logMessage));

    // Node
    final int nodeId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Alarms.NODE_ID));
    String nodeLabel =
        cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.NODE_LABEL));
    TextView node = (TextView) getActivity().findViewById(R.id.alarm_node);
    node.setText(nodeLabel + " (#" + nodeId + ")");
    node.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ActivityUtils.showNodeDetails(getActivity(), nodeId);
      }
    });

    // Service type
    int serviceTypeId =
        cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Alarms.SERVICE_TYPE_ID));
    String serviceTypeName =
        cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.SERVICE_TYPE_NAME));
    TextView serviceType = (TextView) getActivity().findViewById(R.id.alarm_service_type);
    if (serviceTypeName != null) {
      serviceType.setText(serviceTypeName + " (#" + serviceTypeId + ")");
    } else {
      detailsLayout.removeView(serviceType);
      TextView title =
          (TextView) getActivity().findViewById(R.id.alarm_service_type_title);
      detailsLayout.removeView(title);
    }

    // Last event
    String lastEventTimeString =
        cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.LAST_EVENT_TIME));
    final int lastEventId =
        cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Alarms.LAST_EVENT_ID));
    String lastEventSeverity =
        cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.LAST_EVENT_SEVERITY));
    TextView lastEvent = (TextView) getActivity().findViewById(R.id.alarm_last_event);
    lastEvent.setText("#" + lastEventId + " " + lastEventSeverity + "\n" + lastEventTimeString);
    lastEvent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ActivityUtils.showEventDetails(getActivity(), lastEventId);
      }
    });

    isAcked = ackTime != null;
    updateMenu(isAcked);
  }

  private class AcknowledgementTask extends AsyncTask<Void, Void, Alarm> {

    private final MenuItem ackMenuItem = menu.findItem(R.id.menu_ack_alarm);
    private AlarmDetailsFragment fragment;
    private Exception lastException;

    public AcknowledgementTask(AlarmDetailsFragment fragment) {
      this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
      if (ackMenuItem != null) {
        ackMenuItem.setVisible(false);
      }
    }

    @Override
    protected Alarm doInBackground(Void... voids) {
      try {
        return server.alarmSetAck(alarmId, true);
      } catch (RetrofitError e) {
        lastException = e;
        Log.e(TAG, "Error occurred during acknowledgement process!", e);
        return null;
      }
    }

    @Override
    protected void onPostExecute(Alarm alarm) {
      if (alarm != null) {
        Toast.makeText(getActivity(),
                       String.format(getString(R.string.alarm_ack_success), alarmId),
                       Toast.LENGTH_LONG).show();

        // Updating database
        ContentValues[] values = new ContentValues[1];
        values[0] = ContentValuesGenerator.generate(alarm);
        ContentResolver contentResolver = getActivity().getContentResolver();
        contentResolver.bulkInsert(Contract.Alarms.CONTENT_URI, values);

        // Updating details view
        loaderManager.restartLoader(LOADER_ID, null, fragment);
      } else {
        Toast.makeText(getActivity(), "Error occurred during acknowledgement process!",
                       Toast.LENGTH_LONG).show();
        updateMenu(false);
      }
    }

  }

  private class UnacknowledgementTask extends AsyncTask<Void, Void, Alarm> {

    private final MenuItem unackMenuItem = menu.findItem(R.id.menu_unack_alarm);
    private AlarmDetailsFragment fragment;
    private Exception lastException;

    public UnacknowledgementTask(AlarmDetailsFragment fragment) {
      this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
      if (unackMenuItem != null) {
        unackMenuItem.setVisible(false);
      }
    }

    @Override
    protected Alarm doInBackground(Void... voids) {
      try {
        return server.alarmSetAck(alarmId, false);
      } catch (RetrofitError e) {
        lastException = e;
        Log.e(TAG, "Error occurred during unacknowledgement process!", e);
        return null;
      }
    }

    @Override
    protected void onPostExecute(Alarm alarm) {
      if (alarm != null) {
        Toast.makeText(getActivity(),
                       String.format(getString(R.string.alarm_unack_success), alarmId),
                       Toast.LENGTH_LONG).show();

        // Updating database
        ContentValues[] values = new ContentValues[1];
        values[0] = ContentValuesGenerator.generate(alarm);
        ContentResolver contentResolver = getActivity().getContentResolver();
        contentResolver.bulkInsert(Contract.Alarms.CONTENT_URI, values);

        // Updating details view
        loaderManager.restartLoader(LOADER_ID, null, fragment);
      } else {
        Toast.makeText(getActivity(), "Error occurred during unacknowledgement process!",
                       Toast.LENGTH_LONG).show();
        updateMenu(true);
      }
    }
  }

}