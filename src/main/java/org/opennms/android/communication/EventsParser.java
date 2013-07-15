package org.opennms.android.communication;

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

                    String id = getXmlNodeForExpression("@id", currentNode).getNodeValue();
                    values.put(Contract.Events.EVENT_ID, id);

                    String description = getXmlNodeForExpression("description", currentNode).getTextContent();
                    if (description != null) values.put(Contract.Events.DESCRIPTION, description);

                    String logMessage = getXmlNodeForExpression("logMessage", currentNode).getTextContent();
                    if (logMessage != null) values.put(Contract.Events.LOG_MESSAGE, logMessage);

                    String severity = getXmlNodeForExpression("@severity", currentNode).getNodeValue();
                    if (severity != null) values.put(Contract.Events.SEVERITY, severity);

                    String host = getXmlNodeForExpression("host", currentNode).getTextContent();
                    if (host != null) values.put(Contract.Events.HOST, host);

                    String ipAddress = getXmlNodeForExpression("ipAddress", currentNode).getTextContent();
                    if (ipAddress != null) values.put(Contract.Events.IP_ADDRESS, ipAddress);

                    String nodeId = getXmlNodeForExpression("nodeId", currentNode).getTextContent();
                    if (nodeId != null) values.put(Contract.Events.NODE_ID, Integer.parseInt(nodeId));

                    String nodeLabel = getXmlNodeForExpression("nodeLabel", currentNode).getTextContent();
                    if (nodeLabel != null) values.put(Contract.Events.NODE_LABEL, nodeLabel);

                    String createTime = getXmlNodeForExpression("createTime", currentNode).getTextContent();
                    if (createTime != null) values.put(Contract.Events.CREATE_TIME, createTime);

                    String serviceTypeId = getXmlNodeForExpression("serviceType/@id", currentNode).getTextContent();
                    if (serviceTypeId != null)
                        values.put(Contract.Events.SERVICE_TYPE_ID, Integer.parseInt(serviceTypeId));

                    String serviceTypeName = getXmlNodeForExpression("serviceType/name", currentNode).getTextContent();
                    if (serviceTypeName != null) values.put(Contract.Events.SERVICE_TYPE_NAME, serviceTypeName);

                    valuesArray.add(values);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return valuesArray;
    }

}
