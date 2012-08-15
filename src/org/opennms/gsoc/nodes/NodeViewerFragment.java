package org.opennms.gsoc.nodes;

import org.opennms.gsoc.R;
import org.opennms.gsoc.model.OnmsNode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class NodeViewerFragment extends SherlockFragment{
	private View viewer = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.viewer = inflater.inflate(R.layout.node_view,
				container, false);
		this.viewer.setFocusableInTouchMode(true);
		this.viewer.requestFocus();

		return this.viewer;
	}

	public void updateUrl(OnmsNode newNode) {
		if(this.viewer != null) {
			TextView idTextView = (TextView)this.viewer.findViewById(R.id.nodeView);
			idTextView.setText(printNode(newNode));
		}
	}

	private String printNode(OnmsNode newNode) {
		StringBuilder builder = new StringBuilder();
		builder.append("Node Id : " + newNode.getId() + "\n");
		builder.append("Label : " + newNode.getLabel() + "\n");
		builder.append("Type : " + newNode.getType() + "\n");
		builder.append("Created Time : " + newNode.getCreateTime() + "\n");
		builder.append("Sys Contact : " + newNode.getSysContact() + "\n");
		builder.append("Label Source : " + newNode.getLabelSource() + "\n");

		return builder.toString();
	}

}
