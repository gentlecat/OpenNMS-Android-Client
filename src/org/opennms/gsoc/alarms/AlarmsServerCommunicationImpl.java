package org.opennms.gsoc.alarms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import org.opennms.gsoc.RestingServerCommunication;
import org.opennms.gsoc.model.OnmsAlarm;

import com.google.resting.component.impl.ServiceResponse;

public class AlarmsServerCommunicationImpl implements AlarmsServerCommunication {

    private Context appContext;

    public AlarmsServerCommunicationImpl(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public ArrayList<OnmsAlarm> getAlarms(String url) throws InterruptedException, ExecutionException, IOException {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> alarmsCommunication = executorService
                .submit(new RestingServerCommunication("alarms", appContext));
        ServiceResponse alarmsServiceResponse = null;
        alarmsServiceResponse = alarmsCommunication.get();

        ArrayList<OnmsAlarm> alarmsList = new ArrayList<OnmsAlarm>();
        if (alarmsServiceResponse != null) {
            alarmsList = AlarmsParser.parse(alarmsServiceResponse.getContentData()
                    .getContentInString());
        }
        return alarmsList;
    }
}
