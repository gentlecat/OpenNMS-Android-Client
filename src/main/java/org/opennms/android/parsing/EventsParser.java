package org.opennms.android.parsing;

import android.content.ContentValues;
import android.util.Log;

import org.opennms.android.provider.Contract;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;

public class EventsParser extends Parser {

    private static final String TAG = "EventsParser";

    public static ArrayList<ContentValues> parseMultiple(String xml) {
        ArrayList<ContentValues> valuesArray = new ArrayList<ContentValues>();
        try {
            NodeList nodes = getXmlNodeListForExpression("/events/event", xml);
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
            Node node = getXmlNodeForExpression("/event", xml);
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

        Node id = getXmlNodeForExpression("@id", node);
        values.put(Contract.Events._ID, id.getNodeValue());

        Node description = getXmlNodeForExpression("description", node);
        if (description != null) {
            values.put(Contract.Events.DESCRIPTION, description.getTextContent());
        }

        Node logMessage = getXmlNodeForExpression("logMessage", node);
        if (logMessage != null) {
            values.put(Contract.Events.LOG_MESSAGE, logMessage.getTextContent());
        }

        Node severity = getXmlNodeForExpression("@severity", node);
        if (severity != null) {
            values.put(Contract.Events.SEVERITY, severity.getNodeValue());
        }

        Node host = getXmlNodeForExpression("host", node);
        if (host != null) {
            values.put(Contract.Events.HOST, host.getTextContent());
        }

        Node ipAddress = getXmlNodeForExpression("ipAddress", node);
        if (ipAddress != null) {
            values.put(Contract.Events.IP_ADDRESS, ipAddress.getTextContent());
        }

        Node nodeId = getXmlNodeForExpression("nodeId", node);
        if (nodeId != null) {
            values.put(Contract.Events.NODE_ID,
                       Integer.parseInt(nodeId.getTextContent()));
        }

        Node nodeLabel = getXmlNodeForExpression("nodeLabel", node);
        if (nodeLabel != null) {
            values.put(Contract.Events.NODE_LABEL, nodeLabel.getTextContent());
        }

        Node createTime = getXmlNodeForExpression("createTime", node);
        if (createTime != null) {
            values.put(Contract.Events.CREATE_TIME, createTime.getTextContent());
        }

        Node serviceTypeId = getXmlNodeForExpression("serviceType/@id", node);
        if (serviceTypeId != null) {
            values.put(Contract.Events.SERVICE_TYPE_ID,
                       Integer.parseInt(serviceTypeId.getTextContent()));
        }

        Node serviceTypeName = getXmlNodeForExpression("serviceType/name", node);
        if (serviceTypeName != null) {
            values.put(Contract.Events.SERVICE_TYPE_NAME,
                       serviceTypeName.getTextContent());
        }

        return values;
    }
}
