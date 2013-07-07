package org.opennms.android.communication.nodes;

import android.content.Context;
import com.google.resting.component.impl.ServiceResponse;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.dao.nodes.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NodesServerCommunication {

    private final Context appContext;

    public NodesServerCommunication(Context appContext) {
        this.appContext = appContext;
    }

    public ArrayList<Node> getNodes(String url) throws InterruptedException, ExecutionException, IOException {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> nodesCommunication = executorService
                .submit(new ServerCommunication(url, appContext));
        ServiceResponse nodesServiceResponse = nodesCommunication.get();
        ArrayList<Node> nodesList = new ArrayList<Node>();
        if (nodesServiceResponse != null) {
            nodesList = NodesParser.parse(nodesServiceResponse.getContentData().getContentInString());
        }
        return nodesList;
    }
}
