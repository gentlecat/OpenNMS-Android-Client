package org.opennms.android.communication.outages;

import org.opennms.android.dao.outages.Outage;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface OutagesServerCommunication {

    List<Outage> getOutages(String url) throws InterruptedException, ExecutionException;

}
