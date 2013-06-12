package org.opennms.android.tests.ui;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import com.jayway.android.robotium.solo.Solo;
import com.squareup.spoon.Spoon;
import org.opennms.android.R;
import org.opennms.android.ui.MainActivity;

import java.util.ArrayList;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;
    private MainActivity activity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);

        if (solo.searchText(solo.getString(R.string.welcome_message))) {
            solo.clickOnButton(solo.getString(R.string.welcome_message_neg_button));
        }
    }

    public void testAboutDialog() throws Exception {
        Spoon.screenshot(activity, "initial_state");

        solo.clickOnMenuItem(solo.getString(R.string.about), true);
        assertTrue("About dialog text not found", solo.searchText(solo.getString(R.string.about_info)));
        Spoon.screenshot(activity, "dialog_opened");

        Button dialogCloseButton = (Button) solo.getView(android.R.id.button3);
        solo.clickOnButton(solo.getString(R.string.close_dialog));
        solo.sleep(200);
        Spoon.screenshot(activity, "dialog_closed");

        ArrayList<View> currentViews = solo.getCurrentViews();
        assertFalse("Not returned to MainActivity", currentViews.contains(dialogCloseButton));
    }

    public void testSettings() throws Exception {
        Spoon.screenshot(activity, "initial_state");

        solo.clickOnMenuItem(solo.getString(R.string.settings), true);
        assertTrue("SettingsActivity was not found", solo.waitForActivity("SettingsActivity"));
        Spoon.screenshot(activity, "settings_activity_opened");
    }

    public void testNavigation() throws Exception {
        Spoon.screenshot(activity, "initial_state");

        // Nodes
        activity.runOnUiThread(new Runnable() {
            public void run() {
                solo.clickOnActionBarHomeButton();
            }
        });
        String nodesString = solo.getString(R.string.nodes);
        solo.clickOnText(nodesString);
        solo.waitForText(nodesString);
        Spoon.screenshot(activity, "nodes_opened");

        // Outages
        activity.runOnUiThread(new Runnable() {
            public void run() {
                solo.clickOnActionBarHomeButton();
            }
        });
        String outagesString = solo.getString(R.string.outages);
        solo.clickOnText(outagesString);
        solo.waitForText(outagesString);
        Spoon.screenshot(activity, "outages_opened");

        // Events
        activity.runOnUiThread(new Runnable() {
            public void run() {
                solo.clickOnActionBarHomeButton();
            }
        });
        String eventsString = solo.getString(R.string.events);
        solo.clickOnText(eventsString);
        solo.waitForText(eventsString);
        Spoon.screenshot(activity, "events_opened");

        // Alarms
        activity.runOnUiThread(new Runnable() {
            public void run() {
                solo.clickOnActionBarHomeButton();
            }
        });
        String alarmsString = solo.getString(R.string.alarms);
        solo.clickOnText(alarmsString);
        solo.waitForText(alarmsString);
        Spoon.screenshot(activity, "end_state");
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}