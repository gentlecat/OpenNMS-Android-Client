package org.opennms.android.communication.outages;

import android.content.Context;
import com.google.resting.component.impl.ServiceResponse;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.dao.outages.Outage;

import java.util.ArrayList;
import java.util.concurrent.*;

public class OutagesServerCommunication {

    private final Context appContext;

    public OutagesServerCommunication(Context appContext) {
        this.appContext = appContext;
    }

    public ArrayList<Outage> getOutages(String url, long timeout)
            throws InterruptedException, ExecutionException, TimeoutException {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> outagesCommunication = executorService
                .submit(new ServerCommunication(url, appContext));
        ServiceResponse outages = outagesCommunication.get(timeout, TimeUnit.SECONDS);
        ArrayList<Outage> outagesList = new ArrayList<Outage>();
        if (outages != null) {
            outagesList = OutagesParser.parse(outages.getContentData().getContentInString());
        }
        return outagesList;
    }

}
