package org.opennms.gsoc.outages;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.opennms.gsoc.model.OnmsOutage;

public interface OutagesServerCommunication {

	ArrayList<OnmsOutage> getOutages(String url) throws InterruptedException, ExecutionException;

}
