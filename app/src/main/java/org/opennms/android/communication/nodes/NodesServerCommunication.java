package org.opennms.android.communication.nodes;

import org.opennms.android.dao.nodes.Node;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface NodesServerCommunication {

    List<Node> getNodes(String url) throws InterruptedException, ExecutionException, IOException;

}
