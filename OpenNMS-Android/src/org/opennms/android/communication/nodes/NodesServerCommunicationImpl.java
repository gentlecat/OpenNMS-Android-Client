package org.opennms.android.communication.nodes;

import android.content.Context;
import com.google.resting.component.impl.ServiceResponse;
import org.opennms.android.dao.nodes.Node;
import org.opennms.android.util.RestingServerCommunication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NodesServerCommunicationImpl implements NodesServerCommunication {

    private final Context appContext;

    public NodesServerCommunicationImpl(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public ArrayList<Node> getNodes(String url) throws InterruptedException, ExecutionException, IOException {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> nodesCommunication = executorService
                .submit(new RestingServerCommunication("nodes", appContext));
        ServiceResponse nodesServiceResponse = nodesCommunication.get();
        ArrayList<Node> nodesList = new ArrayList<Node>();
        if (nodesServiceResponse != null) {
            nodesList = NodesParser.parse(nodesServiceResponse.getContentData().getContentInString());
        }
        return nodesList;
    }
}
