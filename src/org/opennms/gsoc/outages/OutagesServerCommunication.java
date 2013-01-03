package org.opennms.gsoc.outages;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface OutagesServerCommunication {

    List<Outage> getOutages(String url)
            throws InterruptedException, ExecutionException;

}
