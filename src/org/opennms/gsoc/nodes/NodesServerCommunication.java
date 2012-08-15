package org.opennms.gsoc.nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.opennms.gsoc.model.OnmsNode;


public interface NodesServerCommunication {
	ArrayList<OnmsNode> getNodes(String url) throws InterruptedException, ExecutionException, IOException;
}
