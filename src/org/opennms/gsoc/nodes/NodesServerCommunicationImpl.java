package org.opennms.gsoc.nodes;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.opennms.gsoc.RestingServerCommunication;
import org.opennms.gsoc.model.OnmsNode;

import com.google.resting.component.impl.ServiceResponse;

public class NodesServerCommunicationImpl implements NodesServerCommunication {

	@Override
	public ArrayList<OnmsNode> getNodes(String url) {
		final ExecutorService executorService = Executors.newCachedThreadPool();
		Future<ServiceResponse> nodesCommunication = executorService
				.submit(new RestingServerCommunication("nodes"));
		ServiceResponse nodesServiceResponse = null;
		try {
			nodesServiceResponse = nodesCommunication.get();
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		}

		ArrayList<OnmsNode> nodesList = new ArrayList<OnmsNode>();
		nodesList = NodesParser.parse(nodesServiceResponse.getContentData().getContentInString());
		return nodesList;
	}
}
