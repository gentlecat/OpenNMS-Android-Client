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
                    Event event = new Event(Integer.parseInt(id));

                    Node description = getXmlNodeForExpression(EVENT_DESCRIPTION, currentNode);
                    if (description != null) event.setDescription(description.getTextContent());

                    Node logMessage = getXmlNodeForExpression(EVENT_LOG_MESSAGE, currentNode);
                    if (logMessage != null) event.setLogMessage(logMessage.getTextContent());

                    Node severity = getXmlNodeForExpression(EVENT_SEVERITY, currentNode);
                    if (severity != null) event.setSeverity(severity.getNodeValue());

                    Node host = getXmlNodeForExpression(EVENT_HOST, currentNode);
                    if (host != null) event.setHost(host.getTextContent());

                    Node ipAddress = getXmlNodeForExpression(EVENT_IP_ADDRESS, currentNode);
                    if (ipAddress != null) event.setIpAddress(ipAddress.getTextContent());

                    Node nodeId = getXmlNodeForExpression(EVENT_NODE_ID, currentNode);
                    if (nodeId != null) event.setNodeId(Integer.parseInt(nodeId.getTextContent()));

                    Node nodeLabel = getXmlNodeForExpression(EVENT_NODE_LABEL, currentNode);
                    if (nodeLabel != null) event.setNodeLabel(nodeLabel.getTextContent());

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
