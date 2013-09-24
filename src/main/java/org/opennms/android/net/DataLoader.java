package org.opennms.android.net;

import android.content.Context;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Helper-class that can be used to access OpenNMS server.
 */
public class DataLoader {

    private Client server;

    /**
     * @param context Application context.
     */
    public DataLoader(Context context) {
        server = new Client(context);
    }

    /**
     * Get list of nodes
     * offset into the result set from which results should start being returned.
     *
     * @param limit  Maximum number of nodes in response.
     * @param offset Offset into the result set from which results should start being returned.
     * @return Server response.
     * @throws IOException
     */
    public Response nodes(int limit, int offset) throws IOException {
        return server.get(String.format("nodes?orderBy=id&limit=%d&offset=%d", limit, offset));
    }

    /**
     * Get list of nodes which names start from the same string specified in {@code searchQuery}
     *
     * @param limit       Maximum number of nodes in response.
     * @param offset      Offset into the result set from which results should start being returned.
     * @param searchQuery Beginning of the nodes' names.
     * @return Server response.
     * @throws IOException
     */
    public Response nodes(int limit, int offset, String searchQuery) throws IOException {
        return server.get(String.format("nodes?orderBy=id&limit=%d&offset=%d&comparator=ilike&label=%s%%25", limit, offset, searchQuery));
    }

    /**
     * Get a specific node
     *
     * @param nodeId Node ID.
     * @return Server response.
     * @throws IOException
     */
    public Response node(long nodeId) throws IOException {
        return server.get(String.format("nodes/%d", nodeId));
    }

    /**
     * Get list of events
     *
     * @param limit  Maximum number of events in response.
     * @param offset Offset into the result set from which results should start being returned.
     * @return Server response.
     * @throws IOException
     */
    public Response events(int limit, int offset) throws IOException {
        return server.get(String.format("events?orderBy=id&order=desc&limit=%d&offset=%d", limit, offset));
    }

    /**
     * Get a specific event
     *
     * @param eventId Event ID.
     * @return Server response.
     * @throws IOException
     */
    public Response event(long eventId) throws IOException {
        return server.get(String.format("events/%d", eventId));
    }

    /**
     * Get list of alarms
     *
     * @param limit  Maximum number of alarms in response.
     * @param offset Offset into the result set from which results should start being returned.
     * @return Server response.
     * @throws IOException
     */
    public Response alarms(int limit, int offset) throws IOException {
        return server.get(String.format("alarms?limit=%d&offset=%d", limit, offset));
    }

    /**
     * Get list of alarms that are related to a specific node
     *
     * @param nodeLabel Label of the node.
     * @return Server response.
     * @throws IOException
     */
    public Response alarmsRelatedToNode(String nodeLabel) throws IOException {
        return server.get("alarms/?query=" + URLEncoder.encode("nodeLabel = '" + nodeLabel + "'"));
    }

    /**
     * Get a specific alarm
     *
     * @param alarmId Alarm ID.
     * @return Server response.
     * @throws IOException
     */
    public Response alarm(long alarmId) throws IOException {
        return server.get(String.format("alarms/%d", alarmId));
    }

    /**
     * Get list of outages
     *
     * @param limit  Maximum number of outages in response.
     * @param offset Offset into the result set from which results should start being returned.
     * @return Server response.
     * @throws IOException
     */
    public Response outages(int limit, int offset) throws IOException {
        return server.get(String.format("outages?limit=%d&offset=%d", limit, offset));
    }

    /**
     * Get list of outages that are related to a specific node
     *
     * @param nodeId Node ID.
     * @return Server response.
     * @throws IOException
     */
    public Response outagesRelatedToNode(long nodeId) throws IOException {
        return server.get(String.format("outages/forNode/%d", nodeId));
    }

    /**
     * Get a specific outage
     *
     * @param outageId Outage ID.
     * @return Server response.
     * @throws IOException
     */
    public Response outage(long outageId) throws IOException {
        return server.get(String.format("outages/%d", outageId));
    }

    /**
     * Get information about user
     *
     * @param name User's name.
     * @return Server response.
     * @throws IOException
     */
    public Response user(String name) throws IOException {
        return server.get(String.format("users/%s", name));
    }

}
