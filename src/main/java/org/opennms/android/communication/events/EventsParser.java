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
    private static final String TAG = "EventsParser";

    public static ArrayList<org.opennms.android.dao.events.Event> parse(String xml) {
        ArrayList<org.opennms.android.dao.events.Event> values = new ArrayList<org.opennms.android.dao.events.Event>();

        NodeList nodes = null;
        try {
            nodes = getXmlNodeListForExpression("/events/event", xml);
        } catch (XPathExpressionException e) {
            Log.i(TAG, e.getMessage(), e);
        }
        try {
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node currentNode = nodes.item(i);

                    String id = getXmlNodeForExpression("@id", currentNode).getNodeValue();
                    Event event = new Event(Integer.parseInt(id));

                    Node description = getXmlNodeForExpression("description", currentNode);
                    if (description != null) event.setDescription(description.getTextContent());

                    Node logMessage = getXmlNodeForExpression("logMessage", currentNode);
                    if (logMessage != null) event.setLogMessage(logMessage.getTextContent());

                    Node severity = getXmlNodeForExpression("@severity", currentNode);
                    if (severity != null) event.setSeverity(severity.getNodeValue());

                    Node host = getXmlNodeForExpression("host", currentNode);
                    if (host != null) event.setHost(host.getTextContent());

                    Node ipAddress = getXmlNodeForExpression("ipAddress", currentNode);
                    if (ipAddress != null) event.setIpAddress(ipAddress.getTextContent());

                    Node nodeId = getXmlNodeForExpression("nodeId", currentNode);
                    if (nodeId != null) event.setNodeId(Integer.parseInt(nodeId.getTextContent()));

                    Node nodeLabel = getXmlNodeForExpression("nodeLabel", currentNode);
                    if (nodeLabel != null) event.setNodeLabel(nodeLabel.getTextContent());

                    Node createTime = getXmlNodeForExpression("createTime", currentNode);
                    if (createTime != null) event.setCreateTime(createTime.getTextContent());

                    Node serviceTypeId = getXmlNodeForExpression("serviceType/@id", currentNode);
                    if (serviceTypeId != null) event.setServiceTypeId(Integer.parseInt(serviceTypeId.getTextContent()));

                    Node serviceTypeName = getXmlNodeForExpression("serviceType/name", currentNode);
                    if (serviceTypeName != null) event.setServiceTypeName(serviceTypeName.getTextContent());

                    values.add(event);
                }
            }
        } catch (XPathExpressionException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (DOMException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return values;
    }

}
