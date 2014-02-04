package org.opennms.android.ui;

import org.opennms.android.sync.AlarmsSyncAdapter;
import org.opennms.android.sync.LoadManager;
import org.opennms.android.ui.alarms.AlarmDetailsActivity;
import org.opennms.android.ui.alarms.AlarmDetailsFragment;
import org.opennms.android.ui.alarms.AlarmsActivity;
import org.opennms.android.ui.alarms.AlarmsListFragment;
import org.opennms.android.ui.events.EventDetailsActivity;
import org.opennms.android.ui.events.EventDetailsFragment;
import org.opennms.android.ui.events.EventsActivity;
import org.opennms.android.ui.events.EventsListFragment;
import org.opennms.android.ui.nodes.NodeDetailsActivity;
import org.opennms.android.ui.nodes.NodeDetailsFragment;
import org.opennms.android.ui.nodes.NodesActivity;
import org.opennms.android.ui.nodes.NodesListFragment;
import org.opennms.android.ui.outages.OutageDetailsActivity;
import org.opennms.android.ui.outages.OutageDetailsFragment;
import org.opennms.android.ui.outages.OutagesActivity;
import org.opennms.android.ui.outages.OutagesListFragment;

import dagger.Module;

@Module(
        injects = {
                LoadManager.class,
                AlarmsSyncAdapter.class,

                BaseActivity.class,
                DetailsActivity.class,
                DetailsFragment.class,
                SettingsActivity.class,

                NodesActivity.class,
                NodesListFragment.class,
                NodeDetailsActivity.class,
                NodeDetailsFragment.class,

                OutagesActivity.class,
                OutagesListFragment.class,
                OutageDetailsActivity.class,
                OutageDetailsFragment.class,

                AlarmsActivity.class,
                AlarmsListFragment.class,
                AlarmDetailsActivity.class,
                AlarmDetailsFragment.class,

                EventsActivity.class,
                EventsListFragment.class,
                EventDetailsActivity.class,
                EventDetailsFragment.class,
        },
        complete = false,
        library = true
)
public class UiModule {
}
