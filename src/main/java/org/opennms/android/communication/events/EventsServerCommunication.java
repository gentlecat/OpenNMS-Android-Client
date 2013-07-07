package org.opennms.android.communication.events;

import android.content.Context;
import com.google.resting.component.impl.ServiceResponse;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.dao.events.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class EventsServerCommunication {

    private final Context appContext;

    public EventsServerCommunication(Context appContext) {
        this.appContext = appContext;
    }

    public ArrayList<Event> getEvents(String url, long timeout)
            throws InterruptedException, ExecutionException, IOException, TimeoutException {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> eventsCommunication = executorService
                .submit(new ServerCommunication(url, appContext));
        ServiceResponse events = eventsCommunication.get(timeout, TimeUnit.SECONDS);
        ArrayList<Event> eventsList = new ArrayList<Event>();
        if (events != null) {
            eventsList = EventsParser.parse(events.getContentData().getContentInString());
        }
        return eventsList;
    }

}
