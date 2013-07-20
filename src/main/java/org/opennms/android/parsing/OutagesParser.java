package org.opennms.android.parsing;

import android.content.ContentValues;
import android.util.Log;
import org.opennms.android.provider.Contract;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class OutagesParser extends Parser {

    private static final String TAG = "OutagesParser";

    public static ArrayList<ContentValues> parse(String xml) {
        ArrayList<ContentValues> valuesArray = new ArrayList<ContentValues>();
        try {
            NodeList nodes = getXmlNodeListForExpression("/outages/outage", xml);
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    ContentValues values = new ContentValues();
                    Node currentNode = nodes.item(i);

                    Node id = getXmlNodeForExpression("@id", currentNode);
                    values.put(Contract.Outages._ID, id.getNodeValue());

                    Node ipAddress = getXmlNodeForExpression("ipAddress", currentNode);
                    if (ipAddress != null) values.put(Contract.Outages.IP_ADDRESS, ipAddress.getTextContent());

                    Node serviceId = getXmlNodeForExpression("monitoredService/@id", currentNode);
                    if (serviceId != null)
                        values.put(Contract.Outages.SERVICE_ID, Integer.parseInt(serviceId.getTextContent()));

                    Node ipInterfaceId = getXmlNodeForExpression("monitoredService/ipInterfaceId", currentNode);
                    if (ipInterfaceId != null)
                        values.put(Contract.Outages.IP_INTERFACE_ID, Integer.parseInt(ipInterfaceId.getTextContent()));

                    Node serviceTypeId = getXmlNodeForExpression("monitoredService/serviceType/@id", currentNode);
                    if (serviceTypeId != null)
                        values.put(Contract.Outages.SERVICE_TYPE_ID, Integer.parseInt(serviceTypeId.getTextContent()));

                    Node serviceTypeName = getXmlNodeForExpression("monitoredService/serviceType/name", currentNode);
                    if (serviceTypeName != null)
                        values.put(Contract.Outages.SERVICE_TYPE_NAME, serviceTypeName.getTextContent());

                    Node lostServiceTime = getXmlNodeForExpression("ifLostService", currentNode);
                    if (lostServiceTime != null)
                        values.put(Contract.Outages.SERVICE_LOST_TIME, lostServiceTime.getTextContent());

                    Node serviceLostEventId = getXmlNodeForExpression("serviceLostEvent/@id", currentNode);
                    if (serviceLostEventId != null)
                        values.put(Contract.Outages.SERVICE_LOST_EVENT_ID, Integer.parseInt(serviceLostEventId.getTextContent()));

                    Node regainedServiceTime = getXmlNodeForExpression("ifRegainedService", currentNode);
                    if (regainedServiceTime != null)
                        values.put(Contract.Outages.SERVICE_REGAINED_TIME, regainedServiceTime.getTextContent());

                    Node serviceRegainedEventId = getXmlNodeForExpression("serviceRegainedEvent/@id", currentNode);
                    if (serviceRegainedEventId != null)
                        values.put(Contract.Outages.SERVICE_REGAINED_EVENT_ID, Integer.parseInt(serviceRegainedEventId.getTextContent()));

                    valuesArray.add(values);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return valuesArray;
    }

}
