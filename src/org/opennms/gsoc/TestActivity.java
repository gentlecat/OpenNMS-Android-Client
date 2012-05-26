package org.opennms.gsoc;

import org.opennms.gsoc.nodes.NodesServerCommunicationImpl;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class TestActivity extends SherlockActivity{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Search");  
	    menu.add(R.string.schedule);  
	    return super.onCreateOptionsMenu(menu);  
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	NodesServerCommunicationImpl nodes = new NodesServerCommunicationImpl();
        //Toast.makeText(this, nodes.getNodes("nodes", 1), Toast.LENGTH_SHORT).show();
        return true;
    }
    
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        openOptionsMenu();
    }
}
