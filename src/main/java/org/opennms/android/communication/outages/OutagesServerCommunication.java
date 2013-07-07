package org.opennms.android.communication.outages;

import android.content.Context;
import com.google.resting.component.impl.ServiceResponse;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.dao.outages.Outage;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OutagesServerCommunication {

    private final Context appContext;

    public OutagesServerCommunication(Context appContext) {
        this.appContext = appContext;
    }

    public ArrayList<Outage> getOutages(String url) throws InterruptedException, ExecutionException {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> outagesCommunication = executorService
                .submit(new ServerCommunication(url, appContext));
        ServiceResponse outages = outagesCommunication.get();
        ArrayList<Outage> outagesList = new ArrayList<Outage>();
        if (outages != null) {
            outagesList = OutagesParser.parse(outages.getContentData().getContentInString());
        }
        return outagesList;
    }

}
