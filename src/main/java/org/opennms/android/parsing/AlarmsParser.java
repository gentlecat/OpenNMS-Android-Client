package org.opennms.android.parsing;

import android.content.ContentValues;
import android.util.Log;
import org.opennms.android.provider.Contract;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class AlarmsParser extends Parser {

    private static final String TAG = "AlarmsParser";

    public static ArrayList<ContentValues> parse(String xml) {
        ArrayList<ContentValues> valuesArray = new ArrayList<ContentValues>();
        try {
            NodeList nodes = getXmlNodeListForExpression("/alarms/alarm", xml);
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    ContentValues values = new ContentValues();
                    Node currentNode = nodes.item(i);

                    String id = getXmlNodeForExpression("@id", currentNode).getNodeValue();
                    values.put(Contract.Alarms._ID, id);

                    Node severity = getXmlNodeForExpression("@severity", currentNode);
                    if (severity != null) values.put(Contract.Alarms.SEVERITY, severity.getNodeValue());

                    Node ackUser = getXmlNodeForExpression("ackUser", currentNode);
                    if (ackUser != null) values.put(Contract.Alarms.ACK_USER, ackUser.getTextContent());

                    Node ackTime = getXmlNodeForExpression("ackTime", currentNode);
                    if (ackTime != null) values.put(Contract.Alarms.ACK_TIME, ackTime.getTextContent());

                    Node logMessage = getXmlNodeForExpression("logMessage", currentNode);
                    if (logMessage != null) values.put(Contract.Alarms.LOG_MESSAGE, logMessage.getTextContent());

                    Node description = getXmlNodeForExpression("description", currentNode);
                    if (description != null) values.put(Contract.Alarms.DESCRIPTION, description.getTextContent());

                    Node firstEventTime = getXmlNodeForExpression("firstEventTime", currentNode);
                    if (firstEventTime != null)
                        values.put(Contract.Alarms.FIRST_EVENT_TIME, firstEventTime.getTextContent());

                    Node lastEventTime = getXmlNodeForExpression("lastEventTime", currentNode);
                    if (lastEventTime != null)
                        values.put(Contract.Alarms.LAST_EVENT_TIME, lastEventTime.getTextContent());

                    Node lastEventId = getXmlNodeForExpression("lastEvent/@id", currentNode);
                    if (lastEventId != null)
                        values.put(Contract.Alarms.LAST_EVENT_ID, Integer.parseInt(lastEventId.getTextContent()));

                    Node lastEventSeverity = getXmlNodeForExpression("lastEvent/@severity", currentNode);
                    if (lastEventSeverity != null)
                        values.put(Contract.Alarms.LAST_EVENT_SEVERITY, lastEventSeverity.getTextContent());

                    Node nodeId = getXmlNodeForExpression("nodeId", currentNode);
                    if (nodeId != null) values.put(Contract.Alarms.NODE_ID, Integer.parseInt(nodeId.getTextContent()));

                    Node nodeLabel = getXmlNodeForExpression("nodeLabel", currentNode);
                    if (nodeLabel != null) values.put(Contract.Alarms.NODE_LABEL, nodeLabel.getTextContent());

                    Node serviceTypeId = getXmlNodeForExpression("serviceType/@id", currentNode);
                    if (serviceTypeId != null)
                        values.put(Contract.Alarms.SERVICE_TYPE_ID, Integer.parseInt(serviceTypeId.getTextContent()));

                    Node serviceTypeName = getXmlNodeForExpression("serviceType/name", currentNode);
                    if (serviceTypeName != null)
                        values.put(Contract.Alarms.SERVICE_TYPE_NAME, serviceTypeName.getTextContent());

                    valuesArray.add(values);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return valuesArray;
    }

}
