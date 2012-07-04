package org.opennms.gsoc;

import org.opennms.gsoc.about.AboutActivity;
import org.opennms.gsoc.nodes.NodesActivity;
import org.opennms.gsoc.outages.OutagesActivity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * The class contains the tab management of the application. The assignment of each tab to its activity is performed through intents. 
 * @author melania galea
 *
 */
public class OpenNMSAndroidAppActivity extends TabActivity {
	private static TabHost tabHost;
	
	public static TabHost getOpenNMSTabHost() {
		return tabHost;
	}

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    Resources res = getResources(); 
	    tabHost = getTabHost(); 
	    TabHost.TabSpec spec;
	    Intent intent;

	    intent = new Intent().setClass(this, AboutActivity.class);
	    spec = tabHost.newTabSpec("about").setIndicator("About",
	                      res.getDrawable(R.drawable.icon_about_tab))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, NodesActivity.class);
	    spec = tabHost.newTabSpec("nodes").setIndicator("Nodes",
	                      res.getDrawable(R.drawable.icon_nodes_tab))
	                  .setContent(intent);
	    
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, OutagesActivity.class);
	    spec = tabHost.newTabSpec("nodes").setIndicator("Outages",
	                      res.getDrawable(R.drawable.icon_outages_tab))
	                  .setContent(intent);
	    
	    tabHost.addTab(spec);
	    
	    tabHost.setCurrentTab(2);
	}

}