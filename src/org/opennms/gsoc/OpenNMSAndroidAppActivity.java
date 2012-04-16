package org.opennms.gsoc;

import org.opennms.gsoc.about.AboutActivity;
import org.opennms.gsoc.nodes.NodesActivity;

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

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    Resources res = getResources(); 
	    TabHost tabHost = getTabHost(); 
	    TabHost.TabSpec spec;
	    Intent intent;

	    intent = new Intent().setClass(this, AboutActivity.class);
	    spec = tabHost.newTabSpec("about").setIndicator("About",
	                      res.getDrawable(R.drawable.o))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, NodesActivity.class);
	    spec = tabHost.newTabSpec("nodes").setIndicator("Nodes",
	                      res.getDrawable(R.drawable.display))
	                  .setContent(intent);
	    
	    tabHost.addTab(spec);
	    tabHost.setCurrentTab(2);
	}

}