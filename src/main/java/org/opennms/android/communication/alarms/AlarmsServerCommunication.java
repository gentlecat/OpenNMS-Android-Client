package org.opennms.android.communication.alarms;

import android.content.Context;
import com.google.resting.component.impl.ServiceResponse;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.dao.alarms.Alarm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class AlarmsServerCommunication {

    private final Context appContext;

    public AlarmsServerCommunication(Context appContext) {
        this.appContext = appContext;
    }

    public ArrayList<Alarm> getAlarms(String url, long timeout)
            throws InterruptedException, ExecutionException, IOException, TimeoutException {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> alarmsCommunication = executorService
                .submit(new ServerCommunication(url, appContext));
        ServiceResponse alarmsServiceResponse = alarmsCommunication.get(timeout, TimeUnit.SECONDS);
        ArrayList<Alarm> alarmsList = new ArrayList<Alarm>();
        if (alarmsServiceResponse != null) {
            alarmsList = AlarmsParser.parse(alarmsServiceResponse.getContentData().getContentInString());
        }
        return alarmsList;
    }

}
