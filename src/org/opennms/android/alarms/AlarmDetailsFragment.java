package org.opennms.android.alarms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.android.R;

public class AlarmDetailsFragment extends SherlockFragment {

    Alarm alarm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_alarm, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateContent();
    }

    public void bindAlarm(Alarm alarm) {
        this.alarm = alarm;
        if (this.isVisible()) updateContent();
    }

    public void updateContent() {
        TextView details = (TextView) getSherlockActivity().findViewById(R.id.alarm_details_text);
        if (details != null && alarm != null) details.setText(alarm.toString());
    }

}