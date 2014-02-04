package org.opennms.android.ui;

import org.opennms.android.sync.AlarmsSyncAdapter;
import org.opennms.android.sync.LoadManager;
import org.opennms.android.ui.alarms.AlarmDetailsFragment;
import org.opennms.android.ui.alarms.AlarmsActivity;
import org.opennms.android.ui.alarms.AlarmsListFragment;
import org.opennms.android.ui.events.EventDetailsFragment;
import org.opennms.android.ui.events.EventsActivity;
import org.opennms.android.ui.events.EventsListFragment;
import org.opennms.android.ui.nodes.NodeDetailsFragment;
import org.opennms.android.ui.nodes.NodesActivity;
import org.opennms.android.ui.nodes.NodesListFragment;
import org.opennms.android.ui.outages.OutageDetailsFragment;
import org.opennms.android.ui.outages.OutagesActivity;
import org.opennms.android.ui.outages.OutagesListFragment;

import dagger.Module;

@Module(
        injects = {
                LoadManager.class,
                AlarmsSyncAdapter.class,

                BaseActivity.class,
                DetailsFragment.class,
                SettingsActivity.class,

                NodesActivity.class,
                NodeDetailsFragment.class,
                NodesListFragment.class,

                OutagesActivity.class,
                OutagesListFragment.class,
                OutageDetailsFragment.class,

                AlarmsActivity.class,
                AlarmsListFragment.class,
                AlarmDetailsFragment.class,

                EventsActivity.class,
                EventsListFragment.class,
                EventDetailsFragment.class,
        },
        complete = false,
        library = true
)
public class UiModule {
}
