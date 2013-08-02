package org.opennms.android.parsing;

import android.content.ContentValues;
import android.util.Log;

import org.opennms.android.provider.Contract;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;

public class AlarmsParser extends Parser {

    private static final String TAG = "AlarmsParser";

    public static ArrayList<ContentValues> parseMultiple(String xml) {
        ArrayList<ContentValues> valuesArray = new ArrayList<ContentValues>();
        try {
            NodeList nodes = getXmlNodeListForExpression("/alarms/alarm", xml);
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    valuesArray.add(getContentValues(nodes.item(i)));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return valuesArray;
    }

    public static ContentValues parseSingle(String xml) {
        try {
            Node node = getXmlNodeForExpression("/alarm", xml);
            if (node != null) {
                return getContentValues(node);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private static ContentValues getContentValues(Node node)
            throws XPathExpressionException {
        ContentValues values = new ContentValues();

        String id = getXmlNodeForExpression("@id", node).getNodeValue();
        values.put(Contract.Alarms._ID, id);

        Node severity = getXmlNodeForExpression("@severity", node);
        if (severity != null) {
            values.put(Contract.Alarms.SEVERITY, severity.getNodeValue());
        }

        Node ackUser = getXmlNodeForExpression("ackUser", node);
        if (ackUser != null) {
            values.put(Contract.Alarms.ACK_USER, ackUser.getTextContent());
        }

        Node ackTime = getXmlNodeForExpression("ackTime", node);
        if (ackTime != null) {
            values.put(Contract.Alarms.ACK_TIME, ackTime.getTextContent());
        }

        Node logMessage = getXmlNodeForExpression("logMessage", node);
        if (logMessage != null) {
            values.put(Contract.Alarms.LOG_MESSAGE, logMessage.getTextContent());
        }

        Node description = getXmlNodeForExpression("description", node);
        if (description != null) {
            values.put(Contract.Alarms.DESCRIPTION, description.getTextContent());
        }

        Node firstEventTime = getXmlNodeForExpression("firstEventTime", node);
        if (firstEventTime != null) {
            values.put(Contract.Alarms.FIRST_EVENT_TIME, firstEventTime.getTextContent());
        }

        Node lastEventTime = getXmlNodeForExpression("lastEventTime", node);
        if (lastEventTime != null) {
            values.put(Contract.Alarms.LAST_EVENT_TIME, lastEventTime.getTextContent());
        }

        Node lastEventId = getXmlNodeForExpression("lastEvent/@id", node);
        if (lastEventId != null) {
            values.put(Contract.Alarms.LAST_EVENT_ID,
                       Integer.parseInt(lastEventId.getTextContent()));
        }

        Node lastEventSeverity = getXmlNodeForExpression("lastEvent/@severity", node);
        if (lastEventSeverity != null) {
            values.put(Contract.Alarms.LAST_EVENT_SEVERITY, lastEventSeverity.getTextContent());
        }

        Node nodeId = getXmlNodeForExpression("nodeId", node);
        if (nodeId != null) {
            values.put(Contract.Alarms.NODE_ID, Integer.parseInt(nodeId.getTextContent()));
        }

        Node nodeLabel = getXmlNodeForExpression("nodeLabel", node);
        if (nodeLabel != null) {
            values.put(Contract.Alarms.NODE_LABEL, nodeLabel.getTextContent());
        }

        Node serviceTypeId = getXmlNodeForExpression("serviceType/@id", node);
        if (serviceTypeId != null) {
            values.put(Contract.Alarms.SERVICE_TYPE_ID,
                       Integer.parseInt(serviceTypeId.getTextContent()));
        }

        Node serviceTypeName = getXmlNodeForExpression("serviceType/name", node);
        if (serviceTypeName != null) {
            values.put(Contract.Alarms.SERVICE_TYPE_NAME, serviceTypeName.getTextContent());
        }

        return values;
    }

}
