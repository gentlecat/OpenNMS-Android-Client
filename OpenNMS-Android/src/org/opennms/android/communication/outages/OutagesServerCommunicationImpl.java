package org.opennms.android.communication.outages;

import android.content.Context;
import com.google.resting.component.impl.ServiceResponse;
import org.opennms.android.dao.outages.Outage;
import org.opennms.android.util.RestingServerCommunication;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OutagesServerCommunicationImpl implements OutagesServerCommunication {

    private final Context appContext;

    public OutagesServerCommunicationImpl(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public ArrayList<Outage> getOutages(String url) throws InterruptedException, ExecutionException {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> outagesCommunication = executorService
                .submit(new RestingServerCommunication("outages", appContext));
        ServiceResponse outages = outagesCommunication.get();
        ArrayList<Outage> outagesList = new ArrayList<Outage>();
        if (outages != null) {
            outagesList = OutagesParser.parse(outages.getContentData().getContentInString());
        }
        return outagesList;
    }

}
