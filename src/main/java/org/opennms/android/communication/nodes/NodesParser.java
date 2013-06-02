package org.opennms.android.communication.nodes;

import android.util.Log;
import org.opennms.android.communication.Parser;
import org.opennms.android.dao.nodes.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

/**
 * Class performers parsing actions on the nodes retrieved from the demo.opennms.org server.
 *
 * @author melania galea
 */
public class NodesParser extends Parser {

    private static final String NODE_EXPRESSION = "/nodes/node";
    private static final String NODE_ID = "@id";
    private static final String NODE_LABEL = "@label";
    private static final String NODE_TYPE = "@type";
    private static final String NODE_CREATE_TIME = "createTime";
    private static final String NODE_SYS_CONTACT = "sysContact";
    private static final String NODE_LABEL_SOURCE = "labelSource";

    public static ArrayList<Node> parse(String is) {
        ArrayList<Node> values = new ArrayList<Node>();

        NodeList nodes = null;
        try {
            nodes = getXmlNodeListForExpression(NODE_EXPRESSION, is);
        } catch (XPathExpressionException e) {
            Log.i("NodeParser.getXmlNodeListForExpression", e.getMessage());
        }

        try {
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    org.w3c.dom.Node currentNode = nodes.item(i);

                    String id = getXmlNodeForExpression(NODE_ID, currentNode).getNodeValue();
                    Node node = new Node(Integer.parseInt(id));

                    org.w3c.dom.Node label = getXmlNodeForExpression(NODE_LABEL, currentNode);
                    if (label != null) node.setLabel(label.getNodeValue());

                    org.w3c.dom.Node type = getXmlNodeForExpression(NODE_TYPE, currentNode);
                    if (type != null) node.setType(type.getNodeValue());

                    org.w3c.dom.Node createTime = getXmlNodeForExpression(NODE_CREATE_TIME, currentNode);
                    if (createTime != null) node.setCreateTime(createTime.getTextContent());

                    org.w3c.dom.Node sysContact = getXmlNodeForExpression(NODE_SYS_CONTACT, currentNode);
                    if (sysContact != null) node.setSysContact(sysContact.getTextContent());

                    org.w3c.dom.Node labelSource = getXmlNodeForExpression(NODE_LABEL_SOURCE, currentNode);
                    if (labelSource != null) node.setLabelSource(labelSource.getTextContent());

                    values.add(node);
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
