package org.opennms.gsoc.nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import org.opennms.gsoc.RestingServerCommunication;
import org.opennms.gsoc.model.OnmsNode;

import com.google.resting.component.impl.ServiceResponse;

public class NodesServerCommunicationImpl implements NodesServerCommunication {

    private Context appContext;

    public NodesServerCommunicationImpl(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public ArrayList<OnmsNode> getNodes(String url) throws InterruptedException, ExecutionException, IOException {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> nodesCommunication = executorService
                .submit(new RestingServerCommunication("nodes", appContext));
        ServiceResponse nodesServiceResponse = null;
        nodesServiceResponse = nodesCommunication.get();

        ArrayList<OnmsNode> nodesList = new ArrayList<OnmsNode>();
        if (nodesServiceResponse != null) {
            nodesList = NodesParser.parse(nodesServiceResponse.getContentData()
                    .getContentInString());
        }
        return nodesList;
    }
}
