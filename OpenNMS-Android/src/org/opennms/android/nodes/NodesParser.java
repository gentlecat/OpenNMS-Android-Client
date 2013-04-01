package org.opennms.android.nodes;

import android.util.Log;
import org.opennms.android.util.Parser;
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
    private static final String NODE_LABEL = "@label";
    private static final String NODE_ID = "@id";
    private static final String NODE_TYPE = "@type";
    private static final String NODE_CREATE_TIME = "createTime";
    private static final String NODE_SYS_CONTACT = "sysContact";
    private static final String NODE_LABEL_SOURCE = "labelSource";

    public static ArrayList<Node> parse(String is) {
        ArrayList<Node> values = new ArrayList<Node>();

        NodeList nodes = null;
        try {
            nodes = getXmlNodeSetForExpression(NodesParser.NODE_EXPRESSION, is);
        } catch (XPathExpressionException e) {
            Log.i("NodeParser.getXmlNodeSetForExpression", e.getMessage());
        }

        try {
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    org.w3c.dom.Node currentNode = nodes.item(i);
                    org.w3c.dom.Node label = getXmlNodeForExpression(NodesParser.NODE_LABEL, currentNode);
                    org.w3c.dom.Node id = getXmlNodeForExpression(NodesParser.NODE_ID, currentNode);
                    org.w3c.dom.Node type = getXmlNodeForExpression(NodesParser.NODE_TYPE, currentNode);
                    org.w3c.dom.Node createTime = getXmlNodeForExpression(NodesParser.NODE_CREATE_TIME, currentNode);
                    org.w3c.dom.Node sysContact = getXmlNodeForExpression(NodesParser.NODE_SYS_CONTACT, currentNode);
                    org.w3c.dom.Node labelSource = getXmlNodeForExpression(NodesParser.NODE_LABEL_SOURCE, currentNode);
                    Node node = new Node(
                            Integer.parseInt(id.getNodeValue()),
                            label.getNodeValue(),
                            type.getNodeValue(),
                            createTime.getTextContent(),
                           "",
                            labelSource.getTextContent()
                    );
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
