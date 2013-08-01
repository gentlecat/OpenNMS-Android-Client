package org.opennms.android.parsing;

import android.content.ContentValues;
import android.util.Log;

import org.opennms.android.provider.Contract;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class EventsParser extends Parser {

    private static final String TAG = "EventsParser";

    public static ArrayList<ContentValues> parse(String xml) {
        ArrayList<ContentValues> valuesArray = new ArrayList<ContentValues>();
        try {
            NodeList nodes = getXmlNodeListForExpression("/events/event", xml);
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    ContentValues values = new ContentValues();
                    Node currentNode = nodes.item(i);

                    Node id = getXmlNodeForExpression("@id", currentNode);
                    values.put(Contract.Events._ID, id.getNodeValue());

                    Node description = getXmlNodeForExpression("description", currentNode);
                    if (description != null) {
                        values.put(Contract.Events.DESCRIPTION, description.getTextContent());
                    }

                    Node logMessage = getXmlNodeForExpression("logMessage", currentNode);
                    if (logMessage != null) {
                        values.put(Contract.Events.LOG_MESSAGE, logMessage.getTextContent());
                    }

                    Node severity = getXmlNodeForExpression("@severity", currentNode);
                    if (severity != null) {
                        values.put(Contract.Events.SEVERITY, severity.getNodeValue());
                    }

                    Node host = getXmlNodeForExpression("host", currentNode);
                    if (host != null) {
                        values.put(Contract.Events.HOST, host.getTextContent());
                    }

                    Node ipAddress = getXmlNodeForExpression("ipAddress", currentNode);
                    if (ipAddress != null) {
                        values.put(Contract.Events.IP_ADDRESS, ipAddress.getTextContent());
                    }

                    Node nodeId = getXmlNodeForExpression("nodeId", currentNode);
                    if (nodeId != null) {
                        values.put(Contract.Events.NODE_ID,
                                   Integer.parseInt(nodeId.getTextContent()));
                    }

                    Node nodeLabel = getXmlNodeForExpression("nodeLabel", currentNode);
                    if (nodeLabel != null) {
                        values.put(Contract.Events.NODE_LABEL, nodeLabel.getTextContent());
                    }

                    Node createTime = getXmlNodeForExpression("createTime", currentNode);
                    if (createTime != null) {
                        values.put(Contract.Events.CREATE_TIME, createTime.getTextContent());
                    }

                    Node serviceTypeId = getXmlNodeForExpression("serviceType/@id", currentNode);
                    if (serviceTypeId != null) {
                        values.put(Contract.Events.SERVICE_TYPE_ID,
                                   Integer.parseInt(serviceTypeId.getTextContent()));
                    }

                    Node serviceTypeName = getXmlNodeForExpression("serviceType/name", currentNode);
                    if (serviceTypeName != null) {
                        values.put(Contract.Events.SERVICE_TYPE_NAME,
                                   serviceTypeName.getTextContent());
                    }

                    valuesArray.add(values);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return valuesArray;
    }

}
