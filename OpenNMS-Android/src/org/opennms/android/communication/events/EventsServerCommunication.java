package org.opennms.android.communication.events;

import org.opennms.android.dao.events.Event;
import org.opennms.android.dao.outages.Outage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface EventsServerCommunication {

    List<Event> getEvents(String url) throws InterruptedException, ExecutionException, IOException;

}
