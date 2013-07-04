package org.opennms.android.communication.alarms;

import android.util.Log;
import org.opennms.android.communication.Parser;
import org.opennms.android.dao.alarms.Alarm;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

public class AlarmsParser extends Parser {
    private static final String TAG = "AlarmsParser";

    public static ArrayList<Alarm> parse(String input) {
        ArrayList<Alarm> values = new ArrayList<Alarm>();

        NodeList nodes = null;
        try {
            nodes = getXmlNodeListForExpression("/alarms/alarm", input);
        } catch (XPathExpressionException e) {
            Log.i(TAG, e.getMessage());
        }
        try {
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node currentNode = nodes.item(i);

                    String id = getXmlNodeForExpression("@id", currentNode).getNodeValue();
                    Alarm alarm = new Alarm(Integer.parseInt(id));

                    Node severity = getXmlNodeForExpression("@severity", currentNode);
                    if (severity != null) alarm.setSeverity(severity.getNodeValue());

                    Node logMessage = getXmlNodeForExpression("logMessage", currentNode);
                    if (logMessage != null) alarm.setLogMessage(logMessage.getTextContent());

                    Node description = getXmlNodeForExpression("description", currentNode);
                    if (description != null) alarm.setDescription(description.getTextContent());

                    Node firstEventTime = getXmlNodeForExpression("firstEventTime", currentNode);
                    if (firstEventTime != null) alarm.setFirstEventTime(firstEventTime.getTextContent());

                    Node lastEventTime = getXmlNodeForExpression("lastEventTime", currentNode);
                    if (lastEventTime != null) alarm.setLastEventTime(lastEventTime.getTextContent());

                    Node lastEventId = getXmlNodeForExpression("lastEvent/@id", currentNode);
                    if (lastEventId != null) alarm.setLastEventId(Integer.parseInt(lastEventId.getTextContent()));

                    Node lastEventSeverity = getXmlNodeForExpression("lastEvent/@severity", currentNode);
                    if (lastEventSeverity != null) alarm.setLastEventSeverity(lastEventSeverity.getTextContent());

                    Node nodeId = getXmlNodeForExpression("nodeId", currentNode);
                    if (nodeId != null) alarm.setNodeId(Integer.parseInt(nodeId.getTextContent()));

                    Node nodeLabel = getXmlNodeForExpression("nodeLabel", currentNode);
                    if (nodeLabel != null) alarm.setNodeLabel(nodeLabel.getTextContent());

                    Node serviceTypeId = getXmlNodeForExpression("serviceType/@id", currentNode);
                    if (serviceTypeId != null) alarm.setServiceTypeId(Integer.parseInt(serviceTypeId.getTextContent()));

                    Node serviceTypeName = getXmlNodeForExpression("serviceType/name", currentNode);
                    if (serviceTypeName != null) alarm.setServiceTypeName(serviceTypeName.getTextContent());

                    values.add(alarm);
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
