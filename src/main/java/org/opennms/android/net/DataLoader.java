package org.opennms.android.net;

import android.content.Context;

import java.io.IOException;

public class DataLoader {

    private Client serverCommunication;

    public DataLoader(Context context) {
        serverCommunication = new Client(context);
    }

    public Response loadNodes(int limit, int offset) throws IOException {
        return serverCommunication.get(String.format("nodes?orderBy=id&limit=%d&offset=%d", limit, offset));
    }

    public Response loadNodes(int limit, int offset, String searchQuery) throws IOException {
        return serverCommunication.get(String.format("nodes?orderBy=id&limit=%d&offset=%d&comparator=ilike&label=%s", limit, offset, searchQuery));
    }

    public Response loadEvents(int limit, int offset) throws IOException {
        return serverCommunication.get(String.format("events?orderBy=id&order=desc&limit=%d&offset=%d", limit, offset));
    }

    public Response loadAlarms(int limit, int offset) throws IOException {
        return serverCommunication.get(String.format("alarms?limit=%d&offset=%d", limit, offset));
    }

    public Response loadOutstandingAlarms(int limit, int offset) throws IOException {
        return serverCommunication.get(String.format("alarms?comparator=eq&alarmAckUser=null&limit=%d&offset=%d", limit, offset));
    }

    public Response loadAcknowledgedAlarms(int limit, int offset) throws IOException {
        // TODO: Fix (not working)
        return serverCommunication.get(String.format("alarms?comparator=ne&alarmAckUser=null&limit=%d&offset=%d", limit, offset));
    }

    public Response loadOutages(int limit, int offset) throws IOException {
        return serverCommunication.get(String.format("outages?limit=%d&offset=%d", limit, offset));
    }

    public Response loadCurrentOutages(int limit, int offset) throws IOException {
        return serverCommunication.get(String.format("outages?ifRegainedService=null&comparator=eq&limit=%d&offset=%d", limit, offset));
    }

    public Response loadResolvedOutages(int limit, int offset) throws IOException {
        // TODO: Fix (not working)
        return serverCommunication.get(String.format("outages?ifRegainedService=null&comparator=ne&limit=%d&offset=%d", limit, offset));
    }

    public Response loadUser(String name) throws IOException {
        return serverCommunication.get(String.format("users/%s", name));
    }

}
