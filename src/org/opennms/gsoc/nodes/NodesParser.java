package org.opennms.gsoc.nodes;

import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;

import org.opennms.gsoc.nodes.model.OnmsNode;
import org.opennms.gsoc.util.OnmsParserUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

/**
 * Class performers parsing actions on the nodes retrieved from the demo.opennms.org server.
 * 
 * @author melania galea
 *
 */
public class NodesParser {
	private static final String NODE_EXPRESSION = "/nodes/node";
	private static final String NODE_LABEL = "@label";
	private static final String NODE_ID = "@id";
	private static final String NODE_TYPE = "@type";
	private static final String NODE_CREATE_TIME = "createTime";
	private static final String NODE_SYS_CONTACT = "sysContact";
	private static final String NODE_LABEL_SOURCE = "labelSource";
	
	public static ArrayList<OnmsNode> parse(String is) {
		ArrayList<OnmsNode> values = new ArrayList<OnmsNode>();

		NodeList nodes = null;
		try {
			nodes = OnmsParserUtil.getXmlNodeSetForExpression(NodesParser.NODE_EXPRESSION, is);
		} catch (XPathExpressionException e) {
			Log.i("NodeParser.getXmlNodeSetForExpression", e.getMessage());
		}
		
		try {
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				

				Node label = OnmsParserUtil.getXmlNodeForExpression(NodesParser.NODE_LABEL, node);
				Node id = OnmsParserUtil.getXmlNodeForExpression(NodesParser.NODE_ID, node);
				Node type = OnmsParserUtil.getXmlNodeForExpression(NodesParser.NODE_TYPE, node);
				
				Node createTime = OnmsParserUtil.getXmlNodeForExpression(NodesParser.NODE_CREATE_TIME, node);
				Node labelSource = OnmsParserUtil.getXmlNodeForExpression(NodesParser.NODE_LABEL_SOURCE, node);
				Node sysContact = OnmsParserUtil.getXmlNodeForExpression(NodesParser.NODE_SYS_CONTACT, node);
				
				OnmsNode onmsNode = new OnmsNode(Integer.parseInt(id.getNodeValue()), label.getNodeValue(), type.getNodeValue(), createTime.getTextContent(), sysContact.getTextContent(), labelSource.getTextContent());
				values.add(onmsNode);
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
