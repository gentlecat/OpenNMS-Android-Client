package org.opennms.android.ui;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.android.R;
import org.opennms.android.ui.nodes.NodesActivity;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.tester.android.view.TestMenuItem;

import static org.fest.assertions.api.ANDROID.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class NodesActivityTest {
    private NodesActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(NodesActivity.class).create().get();
    }

    @Test
    public void shouldHaveCorrectAppName() throws Exception {
        String appName = activity.getResources().getString(R.string.app_name);
        assertTrue(appName.equals("OpenNMS"));
    }

    @Test
    public void aboutDialogShouldOpen() throws Exception {
        activity.showAboutDialog();
        TextView aboutText = (TextView) activity.findViewById(R.id.about_info);
        assertThat(aboutText).isVisible();
        assertThat(aboutText).containsText(R.string.about_info);
    }

    @Test
    public void settingsMenuItemShouldOpenNewActivity() throws Exception {
        TestMenuItem settingsMenuItem = new TestMenuItem(R.id.menu_settings);
        settingsMenuItem.click();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertThat(shadowIntent.getComponent().getClassName(), equalTo(SettingsActivity.class.getName()));
    }

}