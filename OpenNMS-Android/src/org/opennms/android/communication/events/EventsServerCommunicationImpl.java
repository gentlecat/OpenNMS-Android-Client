package org.opennms.android.communication.events;

import android.content.Context;
import com.google.resting.component.impl.ServiceResponse;
import org.opennms.android.dao.events.Event;
import org.opennms.android.communication.RestingServerCommunication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EventsServerCommunicationImpl implements EventsServerCommunication {

    private final Context appContext;

    public EventsServerCommunicationImpl(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public ArrayList<Event> getEvents(String url) throws InterruptedException, ExecutionException, IOException {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> eventsCommunication = executorService
                .submit(new RestingServerCommunication("events", appContext));
        ServiceResponse events = eventsCommunication.get();
        ArrayList<Event> eventsList = new ArrayList<Event>();
        if (events != null) {
            eventsList = EventsParser.parse(events.getContentData().getContentInString());
        }
        return eventsList;
    }

}
