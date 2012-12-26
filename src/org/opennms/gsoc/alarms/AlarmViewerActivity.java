package org.opennms.gsoc.alarms;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.widget.TableRow;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.opennms.gsoc.R;
import org.opennms.gsoc.model.Alarm;

/**
 * This activity is used to display alarm details in case dual-pane layout is unavailable
 */
public class AlarmViewerActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_details);

        Alarm alarm = (Alarm) getIntent().getSerializableExtra("alarm");
        showDetails(alarm);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    public void showDetails(Alarm alarm) {
        // Alarm ID
        TextView id = (TextView) findViewById(R.id.alarm_id);
        id.setText(getResources().getString(R.string.alarms_info_id) + alarm.getId());

        // Severity
        TextView severity = (TextView) findViewById(R.id.alarm_severity);
        severity.setText(String.valueOf(alarm.getSeverity()));
        TableRow severityRow = (TableRow) findViewById(R.id.alarm_severity_row);
        // TODO: Check for all possible conditions
        // TODO: Adjust colors
        if (alarm.getSeverity().equals("CLEARED")) {
            severityRow.setBackgroundColor(Color.GREEN);
        } else if (alarm.getSeverity().equals("MINOR")) {
            severityRow.setBackgroundColor(Color.YELLOW);
        } else if (alarm.getSeverity().equals("MAJOR")) {
            severityRow.setBackgroundColor(Color.RED);
        }

        // Description
        TextView description = (TextView) findViewById(R.id.alarm_description);
        description.setText(Html.fromHtml(alarm.getDescription()));

        // Log message
        TextView message = (TextView) findViewById(R.id.alarm_message);
        message.setText(String.valueOf(alarm.getLogMessage()));
    }

}
