package org.opennms.gsoc.nodes;

import java.util.ArrayList;
import java.util.List;


public interface NodesServerCommunication {
	ArrayList<String> getNodes(String url);
}
