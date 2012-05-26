package org.opennms.gsoc.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.opennms.gsoc.ServerConfiguration;

import com.google.resting.Resting;
import com.google.resting.component.RequestParams;
import com.google.resting.component.impl.BasicRequestParams;
import com.google.resting.component.impl.ServiceResponse;

public class NodesServerCommunicationImpl implements NodesServerCommunication {

	@Override
	public ArrayList<String> getNodes(String url) {
		final ExecutorService executorService = Executors.newCachedThreadPool();
		Future<ServiceResponse> nodesCommunication = executorService
				.submit(new RestingNodesServerCommunication());
		String nodes = null;
		try {
			nodes = nodesCommunication.get().toString();
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		}

		ArrayList<String> nodesList = new ArrayList<String>();
		nodesList.add(nodes);
		return nodesList;
	}
}
