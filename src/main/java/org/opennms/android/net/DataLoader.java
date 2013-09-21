package org.opennms.android.net;

import android.content.Context;

import java.io.IOException;

/**
 * Helper-class that can be used to access OpenNMS server.
 */
public class DataLoader {

    private Client serverCommunication;

    public DataLoader(Context context) {
        serverCommunication = new Client(context);
    }

    public Response nodes(int limit, int offset) throws IOException {
        return serverCommunication.get(String.format("nodes?orderBy=id&limit=%d&offset=%d", limit, offset));
    }

    public Response nodes(int limit, int offset, String searchQuery) throws IOException {
        return serverCommunication.get(String.format("nodes?orderBy=id&limit=%d&offset=%d&comparator=ilike&label=%s%%25", limit, offset, searchQuery));
    }

    /**
     * Get a specific node
     *
     * @param nodeId Node ID.
     * @return Server response.
     * @throws IOException
     */
    public Response node(long nodeId) throws IOException {
        return serverCommunication.get(String.format("nodes/%d", nodeId));
    }

    public Response events(int limit, int offset) throws IOException {
        return serverCommunication.get(String.format("events?orderBy=id&order=desc&limit=%d&offset=%d", limit, offset));
    }

    /**
     * Get a specific event
     *
     * @param eventId Event ID.
     * @return Server response.
     * @throws IOException
     */
    public Response event(long eventId) throws IOException {
        return serverCommunication.get(String.format("events/%d", eventId));
    }

    public Response alarms(int limit, int offset) throws IOException {
        return serverCommunication.get(String.format("alarms?limit=%d&offset=%d", limit, offset));
    }

    /**
     * Get a specific alarm.
     *
     * @param alarmId Alarm ID.
     * @return Server response.
     * @throws IOException
     */
    public Response alarm(long alarmId) throws IOException {
        return serverCommunication.get(String.format("alarms/%d", alarmId));
    }

    public Response outages(int limit, int offset) throws IOException {
        return serverCommunication.get(String.format("outages?limit=%d&offset=%d", limit, offset));
    }

    /**
     * Get a specific outage.
     *
     * @param outageId Outage ID.
     * @return Server response.
     * @throws IOException
     */
    public Response outage(long outageId) throws IOException {
        return serverCommunication.get(String.format("outages/%d", outageId));
    }

    public Response user(String name) throws IOException {
        return serverCommunication.get(String.format("users/%s", name));
    }

}
