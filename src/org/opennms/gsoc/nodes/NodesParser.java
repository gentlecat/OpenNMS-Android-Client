package org.opennms.gsoc.nodes;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
	
	public static List<String> parse(InputStream is) {
		List<String> values = new ArrayList<String>();

		NodeList nodes = null;
		try {
			nodes = NodesParser.getXmlNodeSetForExpression(NodesParser.NODE_EXPRESSION, is);
		} catch (XPathExpressionException e) {
			Log.i("NodeParser.getXmlNodeSetForExpression", e.getMessage());
		}

		try {
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);

				Node label = NodesParser.getXmlNodeForExpression(NodesParser.NODE_LABEL, node);
				Node id = NodesParser.getXmlNodeForExpression(NodesParser.NODE_ID, node);
				Node type = NodesParser.getXmlNodeForExpression(NodesParser.NODE_TYPE, node);
				
				values.add("Node label: " + label.getNodeValue() + ", id : " + id.getNodeValue() + ", type: " + type.getNodeValue());
			}
		} catch (XPathExpressionException e) {
			Log.i("node attributes", e.getMessage());
		}

		return values;
	}
	
	private static  NodeList getXmlNodeSetForExpression(String expression,
			InputStream is) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		InputSource inputSource = new InputSource(is);
		return (NodeList) xpath.evaluate(expression, inputSource,
				XPathConstants.NODESET);
	}

	private static Node getXmlNodeForExpression(String expression, Node widgetNode)
			throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		return (Node) xpath.evaluate(expression, widgetNode,
				XPathConstants.NODE);

	}
}
