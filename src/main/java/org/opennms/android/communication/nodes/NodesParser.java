package org.opennms.android.communication.nodes;

import android.util.Log;
import org.opennms.android.communication.Parser;
import org.opennms.android.dao.nodes.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

public class NodesParser extends Parser {
    private static final String TAG = "NodesParser";

    public static ArrayList<Node> parse(String xml) {
        ArrayList<Node> values = new ArrayList<Node>();

        NodeList nodes = null;
        try {
            nodes = getXmlNodeListForExpression("/nodes/node", xml);
        } catch (XPathExpressionException e) {
            Log.i(TAG, e.getMessage(), e);
        }
        try {
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    org.w3c.dom.Node currentNode = nodes.item(i);

                    String id = getXmlNodeForExpression("@id", currentNode).getNodeValue();
                    Node node = new Node(Integer.parseInt(id));

                    org.w3c.dom.Node name = getXmlNodeForExpression("@label", currentNode);
                    if (name != null) node.setName(name.getNodeValue());

                    org.w3c.dom.Node type = getXmlNodeForExpression("@type", currentNode);
                    if (type != null) node.setType(type.getNodeValue());

                    org.w3c.dom.Node createTime = getXmlNodeForExpression("createTime", currentNode);
                    if (createTime != null) node.setCreateTime(createTime.getTextContent());

                    org.w3c.dom.Node sysContact = getXmlNodeForExpression("sysContact", currentNode);
                    if (sysContact != null) node.setSysContact(sysContact.getTextContent());

                    org.w3c.dom.Node labelSource = getXmlNodeForExpression("labelSource", currentNode);
                    if (labelSource != null) node.setLabelSource(labelSource.getTextContent());

                    org.w3c.dom.Node sysDescription = getXmlNodeForExpression("sysDescription", currentNode);
                    if (sysDescription != null) node.setDescription(sysDescription.getTextContent());

                    org.w3c.dom.Node sysLocation = getXmlNodeForExpression("sysLocation", currentNode);
                    if (sysLocation != null) node.setLocation(sysLocation.getTextContent());

                    values.add(node);
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
