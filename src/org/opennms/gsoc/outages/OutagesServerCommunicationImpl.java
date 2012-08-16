package org.opennms.gsoc.outages;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.opennms.gsoc.RestingServerCommunication;
import org.opennms.gsoc.model.OnmsOutage;

import com.google.resting.component.impl.ServiceResponse;

public class OutagesServerCommunicationImpl implements
OutagesServerCommunication {

	@Override
	public ArrayList<OnmsOutage> getOutages(String url) throws InterruptedException, ExecutionException {
		final ExecutorService executorService = Executors.newCachedThreadPool();
		Future<ServiceResponse> outagesCommunication = executorService
				.submit(new RestingServerCommunication("outages"));
		ServiceResponse outages = null;
		outages = outagesCommunication.get();

		ArrayList<OnmsOutage> outagesList = new ArrayList<OnmsOutage>();
		if(outages != null) {
			outagesList = OutagesParser.parse(outages.getContentData().getContentInString());
		}
		return outagesList;
	}

}
