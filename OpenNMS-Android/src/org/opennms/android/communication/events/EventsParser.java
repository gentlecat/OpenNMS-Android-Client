package org.opennms.android.communication.events;

import android.util.Log;
import org.opennms.android.communication.Parser;
import org.opennms.android.dao.events.Event;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

public class EventsParser extends Parser {
    private static final String EVENTS_EXPRESSION = "/events/event";
    private static final String EVENT_ID = "@id";
    private static final String EVENT_SEVERITY = "@severity";
    private static final String EVENT_LOG_MESSAGE = "logMessage";
    private static final String EVENT_DESCRIPTION = "description";
    private static final String EVENT_HOST = "host";
    private static final String EVENT_IP_ADDRESS = "ipAddress";
    private static final String EVENT_NODE_ID = "nodeId";
    private static final String EVENT_NODE_LABEL = "nodeLabel";

    public static ArrayList<org.opennms.android.dao.events.Event> parse(String xml) {
        ArrayList<org.opennms.android.dao.events.Event> values = new ArrayList<org.opennms.android.dao.events.Event>();

        NodeList nodes = null;
        try {
            nodes = getXmlNodeSetForExpression(EVENTS_EXPRESSION, xml);
        } catch (XPathExpressionException e) {
            Log.i("NodeParser.getXmlNodeSetForExpression", e.getMessage());
        }

        try {
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node currentNode = nodes.item(i);
                    String id = getXmlNodeForExpression(EVENT_ID, currentNode).getNodeValue();
                    String description = getXmlNodeForExpression(EVENT_DESCRIPTION, currentNode).getNodeValue();
                    String logMessage = getXmlNodeForExpression(EVENT_LOG_MESSAGE, currentNode).getNodeValue();
                    String severity = getXmlNodeForExpression(EVENT_SEVERITY, currentNode).getNodeValue();
                    String host = getXmlNodeForExpression(EVENT_HOST, currentNode).getNodeValue();
                    String ipAddress = getXmlNodeForExpression(EVENT_IP_ADDRESS, currentNode).getNodeValue();
                    String nodeId = getXmlNodeForExpression(EVENT_NODE_ID, currentNode).getNodeValue();
                    String nodeLabel = getXmlNodeForExpression(EVENT_NODE_LABEL, currentNode).getNodeValue();

                    Event event = new Event(Integer.parseInt(id));
                    if (description != null) event.setDescription(description);
                    if (logMessage != null) event.setLogMessage(logMessage);
                    if (severity != null) event.setSeverity(severity);
                    if (host != null) event.setHost(host);
                    if (ipAddress != null) event.setIpAddress(ipAddress);
                    if (nodeId != null) event.setNodeId(Integer.parseInt(nodeId));
                    if (nodeLabel != null) event.setNodeLabel(nodeLabel);

                    values.add(event);
                }
            }
        } catch (XPathExpressionException e) {
            Log.e("node attributes", e.getMessage(), e);
        } catch (NumberFormatException e) {
            Log.e("node attributes", e.getMessage(), e);
        } catch (DOMException e) {
            Log.e("node attributes", e.getMessage(), e);
        }
        return values;
    }

}
