package org.opennms.android.communication;

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

                    String id = getXmlNodeForExpression("@id", currentNode).getNodeValue();
                    values.put(Contract.Outages._ID, id);

                    String ipAddress = getXmlNodeForExpression("ipAddress", currentNode).getTextContent();
                    if (ipAddress != null) values.put(Contract.Outages.IP_ADDRESS, ipAddress);

                    String serviceId = getXmlNodeForExpression("monitoredService/@id", currentNode).getTextContent();
                    if (serviceId != null) values.put(Contract.Outages.SERVICE_ID, Integer.parseInt(serviceId));

                    String ipInterfaceId = getXmlNodeForExpression("monitoredService/ipInterfaceId", currentNode).getTextContent();
                    if (ipInterfaceId != null)
                        values.put(Contract.Outages.IP_INTERFACE_ID, Integer.parseInt(ipInterfaceId));

                    String serviceTypeId = getXmlNodeForExpression("monitoredService/serviceType/@id", currentNode).getTextContent();
                    if (serviceTypeId != null)
                        values.put(Contract.Outages.SERVICE_TYPE_ID, Integer.parseInt(serviceTypeId));

                    String serviceTypeName = getXmlNodeForExpression("monitoredService/serviceType/name", currentNode).getTextContent();
                    if (serviceTypeName != null) values.put(Contract.Outages.SERVICE_TYPE_NAME, serviceTypeName);

                    String lostServiceTime = getXmlNodeForExpression("ifLostService", currentNode).getTextContent();
                    if (lostServiceTime != null) values.put(Contract.Outages.SERVICE_LOST_TIME, lostServiceTime);

                    String serviceLostEventId = getXmlNodeForExpression("serviceLostEvent/@id", currentNode).getTextContent();
                    if (serviceLostEventId != null)
                        values.put(Contract.Outages.SERVICE_LOST_EVENT_ID, Integer.parseInt(serviceLostEventId));

                    String regainedServiceTime = getXmlNodeForExpression("ifRegainedService", currentNode).getTextContent();
                    if (regainedServiceTime != null)
                        values.put(Contract.Outages.SERVICE_REGAINED_TIME, regainedServiceTime);

                    String serviceRegainedEventId = getXmlNodeForExpression("serviceRegainedEvent/@id", currentNode).getTextContent();
                    if (serviceRegainedEventId != null)
                        values.put(Contract.Outages.SERVICE_REGAINED_EVENT_ID, Integer.parseInt(serviceRegainedEventId));

                    valuesArray.add(values);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return valuesArray;
    }

}
