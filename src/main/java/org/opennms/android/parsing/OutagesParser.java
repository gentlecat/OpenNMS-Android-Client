package org.opennms.android.parsing;

import android.content.ContentValues;
import android.util.Log;

import org.opennms.android.provider.Contract;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;

public class OutagesParser extends Parser {

    private static final String TAG = "OutagesParser";

    public static ArrayList<ContentValues> parseMultiple(String xml) {
        ArrayList<ContentValues> valuesArray = new ArrayList<ContentValues>();
        try {
            NodeList nodes = getXmlNodeListForExpression("/outages/outage", xml);
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
            Node node = getXmlNodeForExpression("/outage", xml);
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
        values.put(Contract.Outages._ID, id.getNodeValue());

        Node ipAddress = getXmlNodeForExpression("ipAddress", node);
        if (ipAddress != null) {
            values.put(Contract.Outages.IP_ADDRESS, ipAddress.getTextContent());
        }

        Node serviceId = getXmlNodeForExpression("monitoredService/@id", node);
        if (serviceId != null) {
            values.put(Contract.Outages.SERVICE_ID, Integer.parseInt(serviceId.getTextContent()));
        }

        Node ipInterfaceId = getXmlNodeForExpression("monitoredService/ipInterfaceId", node);
        if (ipInterfaceId != null) {
            values.put(Contract.Outages.IP_INTERFACE_ID, Integer.parseInt(ipInterfaceId.getTextContent()));
        }

        Node serviceTypeId = getXmlNodeForExpression("monitoredService/serviceType/@id", node);
        if (serviceTypeId != null) {
            values.put(Contract.Outages.SERVICE_TYPE_ID, Integer.parseInt(serviceTypeId.getTextContent()));
        }

        Node serviceTypeName = getXmlNodeForExpression("monitoredService/serviceType/name", node);
        if (serviceTypeName != null) {
            values.put(Contract.Outages.SERVICE_TYPE_NAME, serviceTypeName.getTextContent());
        }

        Node nodeId = getXmlNodeForExpression("serviceLostEvent/nodeId", node);
        if (nodeId != null) {
            values.put(Contract.Outages.NODE_ID, nodeId.getTextContent());
        }

        Node nodeLabel = getXmlNodeForExpression("serviceLostEvent/nodeLabel", node);
        if (nodeLabel != null) {
            values.put(Contract.Outages.NODE_LABEL, nodeLabel.getTextContent());
        }

        Node lostServiceTime = getXmlNodeForExpression("ifLostService", node);
        if (lostServiceTime != null) {
            values.put(Contract.Outages.SERVICE_LOST_TIME, lostServiceTime.getTextContent());
        }

        Node serviceLostEventId = getXmlNodeForExpression("serviceLostEvent/@id", node);
        if (serviceLostEventId != null) {
            values.put(Contract.Outages.SERVICE_LOST_EVENT_ID, Integer.parseInt(serviceLostEventId.getTextContent()));
        }

        Node regainedServiceTime = getXmlNodeForExpression("ifRegainedService", node);
        if (regainedServiceTime != null) {
            values.put(Contract.Outages.SERVICE_REGAINED_TIME, regainedServiceTime.getTextContent());
        }

        Node serviceRegainedEventId = getXmlNodeForExpression("serviceRegainedEvent/@id", node);
        if (serviceRegainedEventId != null) {
            values.put(Contract.Outages.SERVICE_REGAINED_EVENT_ID, Integer.parseInt(serviceRegainedEventId.getTextContent()));
        }

        return values;
    }
}
