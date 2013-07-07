package org.opennms.android.communication.events;

import android.content.Context;
import com.google.resting.component.impl.ServiceResponse;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.dao.events.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EventsServerCommunication {

    private final Context appContext;

    public EventsServerCommunication(Context appContext) {
        this.appContext = appContext;
    }

    public ArrayList<Event> getEvents(String url) throws InterruptedException, ExecutionException, IOException {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> eventsCommunication = executorService
                .submit(new ServerCommunication("events?orderBy=id&order=desc", appContext));
        ServiceResponse events = eventsCommunication.get();
        ArrayList<Event> eventsList = new ArrayList<Event>();
        if (events != null) {
            eventsList = EventsParser.parse(events.getContentData().getContentInString());
        }
        return eventsList;
    }

}
