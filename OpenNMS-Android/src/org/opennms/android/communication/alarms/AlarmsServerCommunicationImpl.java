package org.opennms.android.communication.alarms;

import android.content.Context;
import com.google.resting.component.impl.ServiceResponse;
import org.opennms.android.dao.alarms.Alarm;
import org.opennms.android.communication.RestingServerCommunication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AlarmsServerCommunicationImpl implements AlarmsServerCommunication {

    private final Context appContext;

    public AlarmsServerCommunicationImpl(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public ArrayList<Alarm> getAlarms(String url) throws InterruptedException, ExecutionException, IOException {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> alarmsCommunication = executorService
                .submit(new RestingServerCommunication("alarms", appContext));
        ServiceResponse alarmsServiceResponse = alarmsCommunication.get();
        ArrayList<Alarm> alarmsList = new ArrayList<Alarm>();
        if (alarmsServiceResponse != null) {
            alarmsList = AlarmsParser.parse(alarmsServiceResponse.getContentData().getContentInString());
        }
        return alarmsList;
    }

}
