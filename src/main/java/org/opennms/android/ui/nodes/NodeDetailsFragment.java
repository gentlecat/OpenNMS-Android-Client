package org.opennms.android.ui.nodes;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.net.Client;
import org.opennms.android.net.Response;
import org.opennms.android.parsing.NodesParser;
import org.opennms.android.provider.Contract;

import java.net.HttpURLConnection;

public class NodeDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "NodeDetailsFragment";
    private static final int LOADER_ID = 0x4;
    private long nodeId;
    private LoaderManager loaderManager;

    // Do not remove
    public NodeDetailsFragment() {
    }

    public NodeDetailsFragment(long nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        return new CursorLoader(getActivity(),
                                Uri.withAppendedPath(Contract.Nodes.CONTENT_URI,
                                                     String.valueOf(nodeId)),
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

        /** Checking if data has been loaded from the DB */
        if (cursor != null && cursor.moveToFirst()) {
            updateContent(cursor);
        } else {
            /** If not, trying to get information from the server */
            new GetDetailsFromServer().execute();
        }

        cursor.close();
    }

    private void showErrorMessage() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                RelativeLayout detailsContainer =
                        (RelativeLayout) getActivity().findViewById(R.id.details_container);
                detailsContainer.removeAllViews();
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(R.layout.details_error, detailsContainer);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loaderManager = getLoaderManager();
        loaderManager.restartLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_loading, container, false);
    }

    public void updateContent(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return;
        }

        RelativeLayout detailsContainer =
                (RelativeLayout) getActivity().findViewById(R.id.details_container);
        detailsContainer.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.node_details, detailsContainer);

        LinearLayout detailsLayout =
                (LinearLayout) getActivity().findViewById(R.id.node_details);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.NAME));
        TextView nameView = (TextView) getActivity().findViewById(R.id.node_name);
        nameView.setText(name);

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Nodes._ID));
        TextView idView = (TextView) getActivity().findViewById(R.id.node_id);
        idView.setText(String.valueOf(id));

        String sysContact = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Nodes.CONTACT));
        TextView sysContactView = (TextView) getActivity().findViewById(R.id.node_contact);
        if (sysContact != null) {
            sysContactView.setText(sysContact);
        } else {
            detailsLayout.removeView(sysContactView);
            TextView title = (TextView) getActivity().findViewById(R.id.node_contact_title);
            detailsLayout.removeView(title);
        }

        String createdTime = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Nodes.CREATED_TIME));
        TextView timeView = (TextView) getActivity().findViewById(R.id.node_creation_time);
        timeView.setText(Utils.parseDate(createdTime, "yyyy-MM-dd'T'HH:mm:ss'.'SSSZ"));

        String labelSource = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Nodes.LABEL_SOURCE));
        TextView labelSourceView =
                (TextView) getActivity().findViewById(R.id.node_label_source);
        if (labelSource != null) {
            labelSourceView.setText(labelSource);
        } else {
            detailsLayout.removeView(labelSourceView);
            TextView title =
                    (TextView) getActivity().findViewById(R.id.node_label_source_title);
            detailsLayout.removeView(title);
        }

        String type = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.TYPE));
        TextView typeView = (TextView) getActivity().findViewById(R.id.node_type);
        if (type != null) {
            typeView.setText(type);
        } else {
            detailsLayout.removeView(typeView);
            TextView title = (TextView) getActivity().findViewById(R.id.node_type_title);
            detailsLayout.removeView(title);
        }

        String desc = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Nodes.DESCRIPTION));
        TextView descView = (TextView) getActivity().findViewById(R.id.node_description);
        if (desc != null) {
            descView.setText(desc);
        } else {
            detailsLayout.removeView(descView);
            TextView title = (TextView) getActivity().findViewById(R.id.node_description_title);
            detailsLayout.removeView(title);
        }

        String location = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Nodes.LOCATION));
        TextView locationView = (TextView) getActivity().findViewById(R.id.node_location);
        if (location != null) {
            locationView.setText(location);
        } else {
            detailsLayout.removeView(locationView);
            TextView title = (TextView) getActivity().findViewById(R.id.node_location_title);
            detailsLayout.removeView(title);
        }

        String sysObjectId = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Nodes.SYS_OBJECT_ID));
        TextView sysObjectIdView =
                (TextView) getActivity().findViewById(R.id.node_sys_object_id);
        if (sysObjectId != null) {
            sysObjectIdView.setText(sysObjectId);
        } else {
            detailsLayout.removeView(sysObjectIdView);
            TextView title = (TextView) getActivity()
                    .findViewById(R.id.node_sys_object_id_title);
            detailsLayout.removeView(title);
        }
    }

    private class GetDetailsFromServer extends AsyncTask<Void, Void, Response> {

        protected Response doInBackground(Void... voids) {
            try {
                return new Client(getActivity()).get("nodes/" + nodeId);
            } catch (Exception e) {
                Log.e(TAG, "Error occurred while loading info about node from server", e);
                showErrorMessage();
                return null;
            }
        }

        protected void onPostExecute(Response response) {
            /** If information is available, updating DB */
            if (response != null) {
                if (response.getMessage() != null
                    && response.getCode() == HttpURLConnection.HTTP_OK) {
                    ContentValues[] values = new ContentValues[1];
                    values[0] = NodesParser.parseSingle(response.getMessage());
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    contentResolver.bulkInsert(Contract.Nodes.CONTENT_URI, values);

                    Cursor newCursor = getActivity().getContentResolver().query(
                            Uri.withAppendedPath(Contract.Nodes.CONTENT_URI,
                                                 String.valueOf(nodeId)),
                            null, null, null, null);
                    updateContent(newCursor);
                    newCursor.close();
                } else {
                    showErrorMessage();
                }
            } else {
                showErrorMessage();
            }
        }
    }

}