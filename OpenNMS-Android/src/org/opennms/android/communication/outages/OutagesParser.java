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

    private static final String OUTAGES_EXPRESSION = "/outages/outage";
    private static final String OUTAGES_IF_LOST_SERVICE = "ifLostService";
    private static final String OUTAGES_ID = "@id";
    private static final String OUTAGES_IF_REGAINED_SERVICE = "ifRegainedService";
    private static final String OUTAGES_IP_ADDRESS = "ipAddress";
    private static final String SERVICE_TYPE_NAME = "monitoredService/serviceType/name";

    public static ArrayList<Outage> parse(String is) {
        ArrayList<Outage> values = new ArrayList<Outage>();

        NodeList nodes = null;
        try {
            nodes = getXmlNodeSetForExpression(OUTAGES_EXPRESSION, is);
        } catch (XPathExpressionException e) {
            Log.i("NodeParser.getXmlNodeSetForExpression", e.getMessage());
        }

        try {
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node currentNode = nodes.item(i);

                    String id = getXmlNodeForExpression(OUTAGES_ID, currentNode).getNodeValue();
                    Outage outage = new Outage(Integer.parseInt(id));

                    Node ipAddress = getXmlNodeForExpression(OUTAGES_IP_ADDRESS, currentNode);
                    if (ipAddress != null) outage.setIpAddress(ipAddress.getTextContent());

                    Node ifLostService = getXmlNodeForExpression(OUTAGES_IF_LOST_SERVICE, currentNode);
                    if (ifLostService != null) outage.setIfLostService(ifLostService.getTextContent());

                    Node ifRegainedService = getXmlNodeForExpression(OUTAGES_IF_REGAINED_SERVICE, currentNode);
                    if (ifRegainedService != null) outage.setIfRegainedService(ifRegainedService.getTextContent());

                    Node serviceTypeName = getXmlNodeForExpression(SERVICE_TYPE_NAME, currentNode);
                    if (serviceTypeName != null) outage.setServiceTypeName(serviceTypeName.getTextContent());

                    values.add(outage);
                }
            }
        } catch (XPathExpressionException e) {
            Log.e("node attributes", e.getMessage(), e);
        } catch (NumberFormatException e) {
            Log.e("node attributes", e.getMessage(), e);
        } catch (DOMException e) {
            Log.e("node attributes", e.getMessage(), e);
        }
        return values;
    }

}
