package org.opennms.android.communication.outages;

import android.util.Log;
import org.opennms.android.communication.Parser;
import org.opennms.android.dao.outages.Outage;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

public class OutagesParser extends Parser {
    private static final String TAG = "OutagesParser";

    public static ArrayList<Outage> parse(String xml) {
        ArrayList<Outage> values = new ArrayList<Outage>();

        NodeList nodes = null;
        try {
            nodes = getXmlNodeListForExpression("/outages/outage", xml);
        } catch (XPathExpressionException e) {
            Log.i(TAG, e.getMessage(), e);
        }
        try {
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node currentNode = nodes.item(i);

                    String id = getXmlNodeForExpression("@id", currentNode).getNodeValue();
                    Outage outage = new Outage(Integer.parseInt(id));

                    Node ipAddress = getXmlNodeForExpression("ipAddress", currentNode);
                    if (ipAddress != null) outage.setIpAddress(ipAddress.getTextContent());

                    Node serviceId = getXmlNodeForExpression("monitoredService/@id", currentNode);
                    if (serviceId != null) outage.setServiceId(Integer.parseInt(serviceId.getTextContent()));

                    Node ipInterfaceId = getXmlNodeForExpression("monitoredService/ipInterfaceId", currentNode);
                    if (ipInterfaceId != null) outage.setIpInterfaceId(Integer.parseInt(ipInterfaceId.getTextContent()));

                    Node serviceTypeId = getXmlNodeForExpression("monitoredService/serviceType/@id", currentNode);
                    if (serviceTypeId != null) outage.setServiceTypeId(Integer.parseInt(serviceTypeId.getTextContent()));

                    Node serviceTypeName = getXmlNodeForExpression("monitoredService/serviceType/name", currentNode);
                    if (serviceTypeName != null) outage.setServiceTypeName(serviceTypeName.getTextContent());

                    Node lostServiceTime = getXmlNodeForExpression("ifLostService", currentNode);
                    if (lostServiceTime != null) outage.setLostServiceTime(lostServiceTime.getTextContent());

                    Node serviceLostEventId = getXmlNodeForExpression("serviceLostEvent/@id", currentNode);
                    if (serviceLostEventId != null) outage.setServiceLostEventId(Integer.parseInt(serviceLostEventId.getTextContent()));

                    Node regainedServiceTime = getXmlNodeForExpression("ifRegainedService", currentNode);
                    if (regainedServiceTime != null) outage.setRegainedServiceTime(regainedServiceTime.getTextContent());

                    Node serviceRegainedEventId = getXmlNodeForExpression("serviceRegainedEvent/@id", currentNode);
                    if (serviceRegainedEventId != null) outage.setServiceRegainedEventId(Integer.parseInt(serviceRegainedEventId.getTextContent()));

                    values.add(outage);
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
